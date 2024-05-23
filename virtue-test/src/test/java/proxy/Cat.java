package proxy;

/**
 * @Author WenBo Zhou
 * @Date 2024/3/27 13:29
 */
public class Cat {

    private String code;

    public Cat() {

    }

    public Cat(String code) {
        this.code = code;
    }

    public void say() {
        System.out.println("cat say");
    }

    public String eat(String food) {
        return code + food;
    }

    public void sleep() {
        System.out.println("cat sleep");
    }

}
