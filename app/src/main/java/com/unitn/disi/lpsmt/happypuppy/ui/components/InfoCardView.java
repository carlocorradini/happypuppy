package com.unitn.disi.lpsmt.happypuppy.ui.components;

import java.net.URL;
import java.util.UUID;

public class InfoCardView {
    private URL image;
    private String username, name;
    private String age;
    private UUID uuid;
    private Long idPuppy;

    public InfoCardView(URL image, String username, String name, String age, UUID uuid) {
        this.image = image;
        this.username = username;
        this.name = name;
        this.age = age;
        this.uuid = uuid;
    }
    public InfoCardView(URL image, String username, String name, String age, Long idPuppy) {
        this.image = image;
        this.username = username;
        this.name = name;
        this.age = age;
        this.idPuppy = idPuppy;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getIdPuppy() {
        return idPuppy;
    }

    public void setIdPuppy(Long idPuppy) {
        this.idPuppy = idPuppy;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
