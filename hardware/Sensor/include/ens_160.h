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
extern ScioSense_ENS160 ens160;
extern bool ens_ready;
bool initENS();
ENSData readENS();

#endif