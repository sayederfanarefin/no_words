package info.sayederfanarefin.location_sharing.model;

/**
 * Created by erfanarefin on 25/08/2017.
 */

public class invite_contacts {

    private boolean isChecked = false;

    private String name;

    public void setChecked(){
        isChecked = true;
    }
    public boolean getChecked(){
        return isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    private String contact;
}
