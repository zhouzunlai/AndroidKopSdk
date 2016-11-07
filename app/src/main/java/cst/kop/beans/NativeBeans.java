package cst.kop.beans;

/**
 * Created by zhou-pc on 2016/9/12.
 */
public class NativeBeans {

    public String title;
    public String content;
    public int id;

    public NativeBeans(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public NativeBeans(String title, String content, int id) {
        this.title = title;
        this.content = content;
        this.id = id;
    }
}
