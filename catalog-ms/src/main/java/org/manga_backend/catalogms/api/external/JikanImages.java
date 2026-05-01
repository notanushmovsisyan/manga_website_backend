package org.manga_backend.catalogms.api.external;

public class JikanImages {
    private JikanJpg jpg;

    public JikanImages(JikanJpg jpg) {
        this.jpg = jpg;
    }

    public JikanJpg getJpg() {
        return jpg;
    }
}
