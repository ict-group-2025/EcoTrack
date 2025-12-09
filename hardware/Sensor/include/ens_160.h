#ifndef ens_160
#define ens_160

#include <ScioSense_ENS160.h>

struct ENSData
{
    int aqi;
    int tvoc;
    int eco2;
    bool valid;
};

bool initENS();
ENSData readENS();

#endif