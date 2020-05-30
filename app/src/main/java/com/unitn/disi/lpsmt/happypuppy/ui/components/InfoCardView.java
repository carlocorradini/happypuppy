package com.unitn.disi.lpsmt.happypuppy.ui.components;

import java.net.URI;
import java.util.UUID;

/**
 * InfoCardView class
 * @author Anthony Farina
 */
public class InfoCardView {
    private URI image;
    private String username, name;
    private String age;
    private UUID uuid;
    private Long idPuppy;

    /**
     *
     * @param image image of user
     * @param username username
     * @param name name
     * @param age age
     * @param uuid id of user
     */
    public InfoCardView(URI image, String username, String name, String age, UUID uuid) {
        this.image = image;
        this.username = username;
        this.name = name;
        this.age = age;
        this.uuid = uuid;
    }

    /**
     *
     * @param image image of puppy
     * @param username name of puppy
     * @param name specie of puppy
     * @param age age of puppy
     * @param idPuppy id of puppy
     */
    public InfoCardView(URI image, String username, String name, String age, Long idPuppy) {
        this.image = image;
        this.username = username;
        this.name = name;
        this.age = age;
        this.idPuppy = idPuppy;
    }

    /**
     *
     * @return UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     *
     * @param uuid uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     *
     * @return idPuppy
     */
    public Long getIdPuppy() {
        return idPuppy;
    }

    /**
     *
     * @return URI
     */
    public URI getImage() {
        return image;
    }

    /**
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return String age
     */
    public String getAge() {
        return age;
    }
}
