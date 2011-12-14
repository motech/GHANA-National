package org.motechproject.functional.util;

import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: karthikm
 * Date: 12/13/11
 * Time: 11:57 PM
 * To change this template use File | Settings | File Templates.
 */

@Component
public class DataGenerator {

       public String randomString(int length) {
        Random rand = new Random();
           String chars="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            buf.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return buf.toString();
    }


    public String getRandPhoneNum() {
        long n =(long) Math.floor(Math.random() * 900000000L) + 100000000L;
        return Long.toBinaryString(n);
    }


}
