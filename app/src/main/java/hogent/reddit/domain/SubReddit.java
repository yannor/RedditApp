package hogent.reddit.domain;

/**
 * Created by Yannick on 21/03/2017.
 */

public class SubReddit {
    private String name;
    private int opened;
    private int sizeList;
    private String firstNameList;
    private int positionList;

    public SubReddit(String subName) {
        this.name = subName;
        this.opened = 0;
    }

    public SubReddit(String subName, int opened) {
        this.name = subName;
        this.opened = opened;
    }

    public SubReddit(String name, int opened, String firstNameList, int sizeList, int positionList) {
        this.name = name;
        this.opened = opened;
        this.sizeList = sizeList;
        this.firstNameList = firstNameList;
        this.positionList = positionList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpened() {
        return opened;
    }

    public int getSizeList() {
        return sizeList;
    }

    public int getPositionList() {
        return positionList;
    }

    public String getFirstNameList() {
        return firstNameList;
    }
}
