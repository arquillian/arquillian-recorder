package org.arquillian.recorder.reporter.model.entry;

import org.arquillian.recorder.reporter.PropertyEntry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents arbitrary text element.<br>
 */
@XmlRootElement(name = "textEntry")
public class TextEntry extends PropertyEntry {

    private String content;

    private String type;

    public TextEntry() {
    }

    public TextEntry(String text) {
        this.content = text;
    }

    public void setContent(String text) {
        this.content = text;
    }

    @XmlElement(required = true)
    public String getContent() {
        return content;
    }

    @XmlAttribute(required = false)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TextEntry textEntry = (TextEntry) o;

        return content.equals(textEntry.content);

    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}
