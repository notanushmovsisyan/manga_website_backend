package org.manga_backend.catalogms.api.external;

public class JikanJpg {
    private String image_url;

    public JikanJpg(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }
}
