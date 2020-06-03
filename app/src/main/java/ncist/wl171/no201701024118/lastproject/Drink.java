package ncist.wl171.no201701024118.lastproject;

import androidx.annotation.NonNull;

public class Drink {
    private String name;
    private String description;
    private  int imageResourceId;

    public Drink(String name, String description, int imageResourceId) {
        this.name = name;
        this.description = description;
        this.imageResourceId = imageResourceId;
    }

    public static final Drink[] drinks={
            new Drink("latte","sepresso with steamed milk",R.drawable.tlatte1),
            new Drink("Cappuccino","Espresso,hot milk,steamed milk foam",R.drawable.cappucion),
       new Drink("filter","beans & breawed fresh",R.drawable.filter1),
};

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
