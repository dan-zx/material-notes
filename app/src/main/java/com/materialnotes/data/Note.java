package com.materialnotes.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Clase que representa una nota de la aplicacación.
 *
 * @author Daniel Pedraza Arcega
 */
public class Note implements Serializable {

    private static final long serialVersionUID = -831930284387787342L;

    private Long id;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Note other = (Note) obj;
        if (content == null) {
            if (other.content != null) return false;
        } else if (!content.equals(other.content)) return false;
        if (createdAt == null) {
            if (other.createdAt != null) return false;
        } else if (!createdAt.equals(other.createdAt)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (title == null) {
            if (other.title != null) return false;
        } else if (!title.equals(other.title)) return false;
        if (updatedAt == null) {
            if (other.updatedAt != null) return false;
        } else if (!updatedAt.equals(other.updatedAt)) return false;
        return true;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Note [id=").append(id).append(", title=").append(title)
                .append(", content=").append(content).append(", createdAt=").append(createdAt)
                .append(", updatedAt=").append(updatedAt).append("]").toString();
    }
}