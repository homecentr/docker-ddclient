package helpers;

import org.xbill.DNS.*;

import java.net.UnknownHostException;

public class DnsClient {
    public static Record[] resolve(String domain, String dnsServer) throws UnknownHostException, TextParseException {
        SimpleResolver resolver = new SimpleResolver(dnsServer);

        Lookup lookup = new Lookup(domain, Type.A);
        lookup.setResolver(resolver);

        return lookup.run();
    }
}
