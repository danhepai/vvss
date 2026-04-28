package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.util.List;

public class ReceiptGenerator {
    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");

        for (OrderItem i : o.getItems()) {
            // Java 11 fix: Use findFirst() instead of toList().get(0)
            // This is more efficient and safer
            Product p = products.stream()
                    .filter(p1 -> i.getProduct().getId() == p1.getId())
                    .findFirst()
                    .orElse(null); // Or handle the error if product isn't found

            if (p != null) {
                sb.append(p.getNume() + ": ")
                        .append(p.getPret())
                        .append(" x ")
                        .append(i.getQuantity())
                        .append(" = ")
                        .append(i.getTotal())
                        .append(" RON\n");
            }
        }
        sb.append("---------------------\nTOTAL: ")
                .append(o.getTotalPrice())
                .append(" RON\n=====================\n");

        return sb.toString();
    }
}