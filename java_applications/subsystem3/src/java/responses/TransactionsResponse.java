package responses;

import entities.Transactions;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "transactions")
public class TransactionsResponse {

    private List<Transactions> transactions;

    @XmlElement(name = "transaction")
    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }
}

