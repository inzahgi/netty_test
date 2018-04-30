package nia.test;

public class JustTest {

    private static JustTest one = new JustTest();

    {
        System.out.println("block A");
    }

    static {
        System.out.println("block B");
    }

    public static void main(String[] args) {
        JustTest two = new JustTest();
    }
}
