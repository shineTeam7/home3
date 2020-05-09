%~d0

cd %~d0%~p0/../project/shine
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/commonBase
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/commonCenter
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/commonLogin
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/commonGame
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/base
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/center
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/game
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/login
call %~d0%~p0/toInstall.bat
cd %~d0%~p0/../project/all
call %~d0%~p0/toPackage.bat
cd %~d0%~p0/../project/all/target
rename all-0.0.1-SNAPSHOT-jar-with-dependencies.jar game.jar
move game.jar %~d0%~p0/../bin/jar
pause