//
// Created by John Tekpor Dzikunu on 4/14/24.
//

#ifndef ANDROID_IP_HTTP_TEST_IPADDRESSFINDER_H
#define ANDROID_IP_HTTP_TEST_IPADDRESSFINDER_H

#include <iostream>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <ifaddrs.h>
#include <cstring>

class IPAddressFinder {
private:
    static bool isGlobalIPv6(const struct sockaddr_in6* addr);
    static bool isPublicIPv4(const struct in_addr* addr);

public:
    static const char* getIPAddress();
};

#endif //ANDROID_IP_HTTP_TEST_IPADDRESSFINDER_H

