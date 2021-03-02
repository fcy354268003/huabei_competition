package com.example.huabei_competition.network.api;

public class XhhEncNew {
    public static void main(String[] args) {
        System.out.println(enc("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjE3IiwiZXhwIjoxNjE1MjcxMjUyLCJ1c2VybmFtZSI6IlwiMTU3NTA0NzhcIiJ9.00v4Ubc9aPKNBbDReJpbkGdQoKwRZxJ7pLycPXL-_-s1"));
        System.out.println("UVdvdVYycG9VMkpQYUVkVE1GTXdVVFJzUjBjeFUwVlRWMWN3ZEM5d1FXWkZlR0o0UlZOSlJ6RkJRMHRaVDNvM1pHcFRRWGhGWWxNNFpHUTNaR1I0Ym14VE5ERQ== ");
        System.out.println(enc("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6IjE3IiwiZXhwIjoxNjE1MjcxMjUyLCJ1c2VybmFtZSI6IlwiMTU3NTA0NzhcIiJ9.00v4Ubc9aPKNBbDReJpbkGdQoKwRZxJ7pLycPXL-_-s1").equals("UVdvdVYycG9VMkpQYUVkVE1GTXdVVFJzUjBjeFUwVlRWMWN3ZEM5d1FXWkZlR0o0UlZOSlJ6RkJRMHRaVDNvM1pHcFRRWGhGWWxNNFpHUTNaR1I0Ym14VE5ERQ=="));
    }

    public static String enc(String body) {
        int jm = 0;
        int Ub = 378551;
        int Ua = 63689;
        int jm2 = 0;
        int len;
        int str_len;
        byte[] res;
        int i, j;
        byte[] fl = new byte[body.length() + 8];
        byte[] _fl = body.getBytes();
        for (i = 0; i < body.length(); i++) {
            fl[i] = _fl[i];
        }
        fl[i] = '\0';
        String ta = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        for (i = 0; fl[i] != '\0'; i++) {
        }
        str_len = i;
        if (str_len % 3 == 0)
            len = str_len / 3 * 4;
        else
            len = (str_len / 3 + 1) * 4;
        res = new byte[len + 1];
        res[len] = '\0';
        for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
            res[i] = (byte) ta.charAt(fl[j] >> 2);
            res[i + 1] = (byte) ta.charAt((fl[j] & 0x3) << 4 | (fl[j + 1] >> 4));
            res[i + 2] = (byte) ta.charAt((fl[j + 1] & 0xf) << 2 | (fl[j + 2] >> 6));
            res[i + 3] = (byte) ta.charAt(fl[j + 2] & 0x3f);
        }
        switch (str_len % 3) {
            case 1:
                res[i - 2] = '=';
                res[i - 1] = '=';
                break;
            case 2:
                res[i - 1] = '=';
                break;
        }
        byte[] fls = res;
        for (i = 0; fls[i] != '\0'; i++) {
        }
        int lfls = i;
        for (i = 0; i < lfls; ) {
            jm = fls[i] + (jm << 2) + (jm << 12) - jm;
            jm2 = jm2 * Ua + fls[i++];
            Ua *= Ub;
        }
        int jm3 = jm + jm2;
        int jm4 = jm & 0x7FFFFFFF;
        int jm5 = jm2 & 0x7FFFFFFF;
        byte[] str = new byte[100];
        int ln = 0;
        String tt = "0Ab1Cd2Ef3Gh4Ij5Kl6Mn7Op8Qr9St.UvWxYzAbCdEfGhIjKlMnOpQrStUvWxYz/";
        while (jm != 1) {
            str[ln++] = (byte) tt.charAt((jm % 64 + 64) % 64);
            jm /= 5;
            jm++;
        }
        while (jm2 != 1) {
            str[ln++] = (byte) tt.charAt((jm2 % 64 + 64) % 64);
            jm2 /= 5;
            jm2++;
        }
        while (jm3 != 1) {
            str[ln++] = (byte) tt.charAt((jm3 % 64 + 64) % 64);
            jm3 /= 5;
            jm3++;
        }
        while (jm4 != 1) {
            str[ln++] = (byte) tt.charAt((jm4 % 64 + 64) % 64);
            jm4 /= 5;
            jm4++;
        }
        while (jm5 != 1) {
            str[ln++] = (byte) tt.charAt((jm5 % 64 + 64) % 64);
            jm5 /= 5;
            jm5++;
        }
        str[ln] = '\0';
        str_len = ln;
        if (str_len % 3 == 0)
            len = str_len / 3 * 4;
        else
            len = (str_len / 3 + 1) * 4;
        res = new byte[len + 1];
        res[len] = '\0';
        for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
            res[i] = (byte) ta.charAt(str[j] >> 2);
            res[i + 1] = (byte) ta.charAt((str[j] & 0x3) << 4 | (str[j + 1] >> 4));
            res[i + 2] = (byte) ta.charAt((str[j + 1] & 0xf) << 2 | (str[j + 2] >> 6));
            res[i + 3] = (byte) ta.charAt(str[j + 2] & 0x3f);
        }
        switch (str_len % 3) {
            case 1:
                res[i - 2] = '\0';
                res[i - 1] = '\0';
                break;
            case 2:
                res[i - 1] = '\0';
                break;
        }
        ta = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        for (i = 0; res[i] != '\0'; i++) {
        }
        str_len = i;
        byte[] ress = new byte[i];
        for (i = 0; i < str_len; i++) {
            ress[i] = res[i];
        }
        res = new byte[str_len + 4];
        for (i = 0; i < str_len; i++) {
            res[i] = ress[i];
        }
        if (str_len % 3 == 0)
            len = str_len / 3 * 4;
        else
            len = (str_len / 3 + 1) * 4;
        str = new byte[len + 1];
        str[len] = '\0';
        for (i = 0, j = 0; i < len - 2; j += 3, i += 4) {
            str[i] = (byte) ta.charAt(res[j] >> 2);
            str[i + 1] = (byte) ta.charAt((res[j] & 0x3) << 4 | (res[j + 1] >> 4));
            str[i + 2] = (byte) ta.charAt((res[j + 1] & 0xf) << 2 | (res[j + 2] >> 6));
            str[i + 3] = (byte) ta.charAt(res[j + 2] & 0x3f);
        }
        switch (str_len % 3) {
            case 1:
                str[i - 2] = '=';
                str[i - 1] = '=';
                break;
            case 2:
                str[i - 1] = '=';
                break;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : str) {
            stringBuilder.append((char) b);
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }
}
