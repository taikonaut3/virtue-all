package org.example.model1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParentObject implements Serializable {

    private String someValue;

    private ChildObject child;

    // getters 和 setters

    public static List<ParentObject> getObjList() {
        return getObjList("");
    }

    public static List<ParentObject> getObjList(String content) {
        ArrayList<ParentObject> parentObjects = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ParentObject parent = new ParentObject();
            parent.setSomeValue(content + " Client Parent Value" + i + i + i);

            ChildObject child = new ChildObject();
            child.setSomeValue(content + " Child Value" + i + i + i);

            GrandchildObject grandchild = new GrandchildObject();
            grandchild.setSomeValue(content + " Grandchild Value" + i + i + i);

            // 将子对象和孙子对象添加到父对象中
            child.setGrandchild(grandchild);
            parent.setChild(child);
            parentObjects.add(parent);
        }
        return parentObjects;
    }

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

    public ChildObject getChild() {
        return child;
    }

    public void setChild(ChildObject child) {
        this.child = child;
    }

}

