//
// Created by John Tekpor Dzikunu on 4/14/24.
//

#include "IPAddressFinder.h"
#pragma once
bool IPAddressFinder::isGlobalIPv6(const struct sockaddr_in6* addr) {
    return (addr->sin6_addr.s6_addr[0] & 0xe0) == 0x20 &&
           !(addr->sin6_addr.s6_addr[0] == 0xfe &&
             (addr->sin6_addr.s6_addr[1] & 0xc0) == 0x80);
}

bool IPAddressFinder::isPublicIPv4(const struct in_addr* addr) {
    unsigned long ip = ntohl(addr->s_addr);
    return !(ip == 0 || ip == 0x7f000001 || // 127.0.0.1
             (ip >= 0xa000000 && ip <= 0xa0ffffff) || // 10.0.0.0 - 10.255.255.255
             (ip >= 0xac100000 && ip <= 0xac1fffff) || // 172.16.0.0 - 172.31.255.255
             (ip >= 0xc0a80000 && ip <= 0xc0a8ffff)); // 192.168.0.0 - 192.168.255.255
}

const char* IPAddressFinder::getIPAddress() {
    struct ifaddrs *ifAddressStruct, *ifa;
    if (getifaddrs(&ifAddressStruct) == -1) {
        std::cerr << "Error in ifAddress\n";
        return nullptr;
    }
    std::unique_ptr<struct ifaddrs, void(*)(struct ifaddrs*)> ifAddress(ifAddressStruct, freeifaddrs);

    const char* ipv6;
    const char* publicIPv4 = nullptr;
    const char* nonLoopBackIPv4 = nullptr;

    for (ifa = ifAddressStruct; ifa != nullptr; ifa = ifa->ifa_next) {
        if (ifa->ifa_addr) {
            if (ifa->ifa_addr->sa_family == AF_INET6) {
                auto *ipv6Address = reinterpret_cast<struct sockaddr_in6*>(ifa->ifa_addr);
                if (isGlobalIPv6(ipv6Address)) {
                    ipv6 = inet_ntop(AF_INET6, &ipv6Address->sin6_addr, nullptr, 0);
                    if (ipv6)
                        return ipv6;
                }
            } else if (ifa->ifa_addr->sa_family == AF_INET) {
                auto *ipv4Address = reinterpret_cast<struct sockaddr_in*>(ifa->ifa_addr);
                if (isPublicIPv4(&ipv4Address->sin_addr)) {
                    publicIPv4 = inet_ntoa(ipv4Address->sin_addr);
                } else if (!nonLoopBackIPv4 && ipv4Address->sin_addr.s_addr != htonl(INADDR_LOOPBACK)) {
                    nonLoopBackIPv4 = inet_ntoa(ipv4Address->sin_addr);
                }
            }
        }
    }

    if (publicIPv4)
        return publicIPv4;
    else if (nonLoopBackIPv4)
        return nonLoopBackIPv4;
    else
        return nullptr;
}
