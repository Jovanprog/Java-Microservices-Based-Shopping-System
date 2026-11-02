package responses;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "statusResponse")
public class StatusResponse {

    private String status;

    @XmlElement
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

