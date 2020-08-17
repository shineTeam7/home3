using System;
using ShineEngine;
using UnityEngine;

/// <summary>
/// 音频控制类
/// </summary>
public class AudioControl
{
	/** 音效音量 */
	private float _soundVolume = 1;
	/** 音乐音量 */
	private float _musicVolume = 1;
	/** 缓动音量系数(0-1) */
	private float _musicTweenRadio=1;

	/** 是否播放音效 */
	private bool _soundSwitch = true;
	/** 是否播放音量 */
	private bool _musicSwitch = true;

	/** 是否暂停music */
	private bool _pauseMusic = false;
	
	private LoadTool _musicLoadTool;
	private AudioSource _music;

	/** 是否暂停UISound */
	private bool _pauseUISound = false;
	
	private LoadTool _uiSoundLoadTool;
	private AudioSource _uiSound;

	private int _tickIndex;

	/** 当前播放的音乐 */
	private int _curMusic;

	/** 下一个要播放的音乐 */
	private int _nextMusic;
	
	/** 最后一首音乐,为了停止以后的恢复播放 */
	private int _lastMusic=-1;

	/** 当前播放的音效 */
	private int _curUISound;

	/** 音乐淡出缓动 */
	private int _musicVolumeTween=-1;

	private ObjectPool<AudioSource> _soundGameObjectPool;

	private SList<AudioSource> _soundList;

	public AudioControl()
	{

	}

	public void init()
	{
		_musicLoadTool=new LoadTool(onLoadMusic);
		_uiSoundLoadTool=new LoadTool(onLoadUISound);

		_soundGameObjectPool=new ObjectPool<AudioSource>(createSoundSource);

		_music=createAudioSource("musicObj",_musicVolume,true);
		_uiSound=createAudioSource("uiSoundObj",_soundVolume,false);
		_soundList=new SList<AudioSource>();

		_tickIndex=TimeDriver.instance.setInterval(onTick,100);

		if(GameC.save.hasInt(LocalSaveType.MusicVolume))
		{
			_musicVolume=GameC.save.getInt(LocalSaveType.MusicVolume) / 1000.0f;
		}

		if(GameC.save.hasInt(LocalSaveType.SoundVolume))
		{
			_soundVolume=GameC.save.getInt(LocalSaveType.SoundVolume) / 1000.0f;
		}
		
		if(GameC.save.hasBool(LocalSaveType.MusicSwitch))
		{
			_musicSwitch=GameC.save.getBool(LocalSaveType.MusicSwitch);
		}

		if(GameC.save.hasBool(LocalSaveType.SoundSwitch))
		{
			_soundSwitch=GameC.save.getBool(LocalSaveType.SoundSwitch);
		}
	}

	public void dispose()
	{
		GameObject.Destroy(_music.gameObject);
		GameObject.Destroy(_uiSound.gameObject);
		TimeDriver.instance.clearInterval(_tickIndex);
	}

	public bool musicSwitch
	{
		get
		{
			return _musicSwitch;
		}
		set
		{
			_musicSwitch=value;
			if(_musicSwitch)
			{
				playLastMusic();
			}
			else
			{
				_music.Stop();
			}
		
			GameC.save.setBool(LocalSaveType.MusicSwitch,value);
		}
	}

	public bool soundSwitch
	{
		get
		{
			return _soundSwitch;
		}
		set
		{
			_soundSwitch=value;
			if(!_soundSwitch)
			{
				_uiSound.Stop();
				for(int i=0;i<_soundList.length();++i)
				{
					AudioSource audioSource=_soundList[i];
					audioSource.Stop();
				}
			}
		
			GameC.save.setBool(LocalSaveType.SoundSwitch,value);
		}
	}

	/// <summary>
	/// 音效音量
	/// </summary>
	public float soundVolume
	{
		get
		{
			return _soundVolume;
		}
		set
		{
			_soundVolume=value;

			_uiSound.volume=_soundVolume;
			for(int i=0;i<_soundList.length();++i)
			{
				AudioSource audioSource=_soundList[i];
				audioSource.volume=_soundVolume;
			}

			GameC.save.setInt(LocalSaveType.SoundVolume,(int)(_soundVolume * 1000));
		}
	}

	/// <summary>
	/// 音乐音量
	/// </summary>
	public float musicVolume
	{
		get
		{
			return _musicVolume;
		}
		set
		{
			_musicVolume=value;

			_music.volume=_musicVolume;

			refreshMusicVolume();

			GameC.save.setInt(LocalSaveType.MusicVolume,(int)(_musicVolume * 1000));
		}
	}

	public bool isMusicPlaying()
	{
		return _music.isPlaying;
	}

	/// <summary>
	/// 播放背景音乐（同时只能播放一个）
	/// </summary>
	/// <param name="name">音乐资源</param>
	/// <param name="force">是否强制重新播放</param>
	/// <param name="loop">是否重复</param>
	public void playMusic(string name, bool force, bool loop=true)
	{
		playMusic(LoadControl.getResourceIDByName(name),force, loop);
	}

	/// <summary>
	/// 播放背景音乐（同时只能播放一个）
	/// </summary>
	/// <param name="id">音乐资源id</param>
	/// <param name="force">是否强制重新播放</param>
	/// <param name="loop">是否重复</param>
	public void playMusic(int id, bool force=false, bool loop = true)
	{
		_music.loop = loop;
		
		if(id<=0)
			return;

		_lastMusic = id;
		
		if(!_musicSwitch)
			return;

		if(!force && isMusicPlaying() && id==_curMusic)
			return;

		stopMusic(true);

		_curMusic=id;
		
		musicVolume=_musicVolume;
		_musicTweenRadio=1;
		refreshMusicVolume();

		_musicLoadTool.loadOne(id);
	}

	/// <summary>
	/// 设置下一个播放的音乐（在当前音乐结束后播放）
	/// </summary>
	/// <param name="id">音乐资源id</param>
	public void playNextMusic(int id)
	{
		if(!isMusicPlaying())
		{
			playMusic(id);
		}
		else
		{
			_nextMusic=id;
		}
	}
	
	/** 播放最后一首音乐 */
	private void playLastMusic()
	{
		if (_lastMusic != -1)
		{
			playMusic(_lastMusic,true);
		}
	}

	private void onLoadMusic()
	{
		_music.clip=LoadControl.getUnityObjectByType<AudioClip>(_curMusic);
		
		//刷新状态
		pauseMusic(_pauseMusic);
	}

	/// <summary>
	/// 停止音乐
	/// </summary>
	/// <param name="isAbs">是否立即停止（否则淡出）</param>
	public void stopMusic(bool isAbs=false)
	{
		Action musicOver=()=>
		{
			_curMusic=-1;
			_music.Stop();

			if(_nextMusic>0)
			{
				int nextMusic=_nextMusic;
				_nextMusic=0;
				playMusic(nextMusic);
			}
		};

		clearMusicVolumeTween();
		if(isAbs)
		{
			musicOver();
		}
		else
		{
			_musicVolumeTween=Tween.normal.create(_musicTweenRadio,0,3000,setMusicFadeOut,()=>
			{
				clearMusicVolumeTween();
				musicOver();
			});
		}
	}
	
	/** 是否暂停音乐 */
	public void pauseMusic(bool v)
	{
		_pauseMusic = v;

		if (_music != null)
		{
			if (v)
			{
				_music.Pause();
			}
			else
			{
				_music.Play();
			}
		}
	}

	/** 是否暂停音效 */
	public void pauseUISound(bool v)
	{
		_pauseUISound = v;

		if (_uiSound != null)
		{
			if (v)
			{
				_uiSound.Pause();
			}
			else
			{
				_uiSound.Play();
			}
		}
	}
	
	/// <summary>
	/// 播放UI音效（同时只能播放一个）
	/// </summary>
	public void playUISound(string name)
	{
		playUISound(LoadControl.getResourceIDByName(name));
	}

	/// <summary>
	/// 播放UI音效（同时只能播放一个）
	/// </summary>
	public void playUISound(int id)
	{
		if(id<=0)
			return;

		if(!_soundSwitch)
			return;

		_uiSoundLoadTool.loadOne(_curUISound=id);
	}

	private void onLoadUISound()
	{
		_uiSound.clip=LoadControl.getUnityObjectByType<AudioClip>(_curUISound);
		
		pauseUISound(_pauseUISound);
	}

	/// <summary>
	/// 播放音效
	/// </summary>
	public void playSound(string name)
	{
		playSound(LoadControl.getResourceIDByName(name));
	}

	/// <summary>
	/// 播放音效
	/// </summary>
	public void playSound(int id)
	{
		if(id<=0)
			return;

		if(!_soundSwitch)
			return;

		LoadControl.loadOne(id,()=>
		{
			AudioClip audioClip=LoadControl.getUnityObjectByType<AudioClip>(id);
			AudioSource audioSource=_soundGameObjectPool.getOne();

			audioSource.gameObject.SetActive(true);
			audioSource.clip=audioClip;
			audioSource.Play();
			_soundList.add(audioSource);
		});
	}

	private void onTick(int interval)
	{
		for(int i=0;i<_soundList.length();++i)
		{
			AudioSource audioSource=_soundList[i];
			if(!audioSource.isPlaying)
			{
				_soundList.remove(i);
				removeSound(audioSource);
				--i;
			}
		}
	}

	private void setMusicFadeOut(float radio)
	{
		_musicTweenRadio=radio;
		refreshMusicVolume();
	}

	private void clearMusicVolumeTween()
	{
		if(_musicVolumeTween!=-1)
		{
			Tween.normal.kill(_musicVolumeTween);
			_musicVolumeTween=-1;
		}
	}

	private void removeSound(AudioSource audioSource)
	{
		audioSource.gameObject.SetActive(false);
		_soundGameObjectPool.back(audioSource);
	}

	private AudioSource createSoundSource()
	{
		return createAudioSource("soundObj",_soundVolume,false);
	}

	private AudioSource createAudioSource(string name, float volume, bool isLoop)
	{
		GameObject obj=new GameObject(name);
		AudioSource audioSource=obj.AddComponent<AudioSource>();
		audioSource.volume=volume;
		audioSource.loop=isLoop;
		obj.transform.SetParent(UIControl.getUIContainer().transform);

		return audioSource;
	}

	/** 刷新音乐音量 */
	private void refreshMusicVolume()
	{
		_music.volume=_musicVolume * _musicTweenRadio;
	}
}