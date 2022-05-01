package top.parak.kraft.core.rpc;

import org.junit.Test;

public class AddressTest {

    @Test
    public void createAddressSucceed() {
        Address address = new Address("192.168.1.131", 10000);
        System.out.println(address.toString());
    }

}