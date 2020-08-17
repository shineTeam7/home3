#include "SInclude.h"
#include <benchmark.h>

/*for (auto _ : state)
	{
		++_a;
	}*/

int _si = 10000;
int _bi = 10;

int _a = 0;

class MTT
{
public:
	int m_a = 10000;

private:

};

static void BM_TestBig(benchmark::State& state)
{
	const int si = _si;
	const int bi = _bi;

	while (state.KeepRunning())
	{
		_a = 0;

		for (int i = 0; i < si; ++i)
		{
			for (int j = 0; j < bi; ++j)
			{
				++_a;
			}
		}
	}
}

static void BM_TestShort(benchmark::State& state)
{
	const int si = _si;
	const int bi = _bi;

	while (state.KeepRunning())
	{
		_a = 0;

		for (int i = 0; i < bi; ++i)
		{
			for (int j = 0; j < si; ++j)
			{
				++_a;
			}
		}
	}
}

static void BM_TestFor1(benchmark::State& state)
{
	while (state.KeepRunning())
	{
		MTT m;

		for (int i = 0; i < m.m_a; ++i)
		{
			++_a;
		}
	}
}

static void BM_TestFor2(benchmark::State& state)
{
	while (state.KeepRunning())
	{
		MTT m;

		const int si = m.m_a;

		for (int i = 0; i < si; ++i)
		{
			++_a;
		}
	}
}

BENCHMARK(BM_TestBig);
BENCHMARK(BM_TestShort);

//BENCHMARK(BM_TestFor1);
//BENCHMARK(BM_TestFor2);

BENCHMARK_MAIN();

//int main()
//{
//
//}
