package io.multi.hello.service.link.url;

/**
 * Base62 인코더/디코더
 *
 * 62진법 (0-9, a-z, A-Z)으로 숫자를 인코딩/디코딩합니다.
 *
 * 특징:
 * - URL-safe (특수문자 없음)
 * - 짧은 문자열 (10진수 대비 압축)
 * - 가독성 좋음 (Base64 대비 특수문자 없음)
 */
public class Base62Encoder {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    /**
     * 숫자를 Base62 문자열로 인코딩
     *
     * @param num 인코딩할 숫자 (양수)
     * @return Base62 인코딩된 문자열
     */
    public static String encode(long num) {
        if (num == 0) {
            return "0";
        }

        if (num < 0) {
            throw new IllegalArgumentException("Number must be non-negative");
        }

        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE);
            sb.append(BASE62_CHARS.charAt(remainder));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    /**
     * Base62 문자열을 숫자로 디코딩
     *
     * @param str 디코딩할 Base62 문자열
     * @return 디코딩된 숫자
     */
    public static long decode(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("String must not be null or empty");
        }

        long num = 0;
        for (char c : str.toCharArray()) {
            int index = BASE62_CHARS.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            num = num * BASE + index;
        }

        return num;
    }

    /**
     * 인코딩 결과의 최소 길이 보장 (패딩)
     *
     * @param num 인코딩할 숫자
     * @param minLength 최소 길이
     * @return 패딩된 Base62 문자열
     */
    public static String encodeWithPadding(long num, int minLength) {
        String encoded = encode(num);
        if (encoded.length() >= minLength) {
            return encoded;
        }

        // '0'으로 왼쪽 패딩
        return "0".repeat(minLength - encoded.length()) + encoded;
    }
}