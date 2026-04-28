package drinkshop.service;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Recipe;
import drinkshop.domain.Stock;
import drinkshop.repository.Repository;
import java.util.stream.Collectors; // Ensure this import is present
import java.util.List;
import java.util.stream.Collectors;

public class StocService {

    private final Repository<Integer, Stock> stocRepo;

    public StocService(Repository<Integer, Stock> stocRepo) {
        this.stocRepo = stocRepo;
    }

    public List<Stock> getAll() {
        return stocRepo.findAll();
    }

    public void add(Stock s) {
        stocRepo.save(s);
    }

    public void update(Stock s) {
        stocRepo.update(s);
    }

    public void delete(int id) {
        stocRepo.delete(id);
    }

    public boolean areSuficient(Recipe recipe) {
        List<IngredientReteta> ingredienteNecesare = recipe.getIngrediente();

        for (IngredientReteta e : ingredienteNecesare) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            double disponibil = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .mapToDouble(Stock::getCantitate)
                    .sum();

            if (disponibil < necesar) {
                return false;
            }
        }
        return true;
    }


    public void consuma(Recipe recipe) {
        if (!areSuficient(recipe)) {
            throw new IllegalStateException("Stock insuficient pentru rețeta.");
        }

        for (IngredientReteta e : recipe.getIngrediente()) {
            String ingredient = e.getDenumire();
            double necesar = e.getCantitate();

            // Change .toList() to .collect(Collectors.toList())
            List<Stock> ingredienteStock = stocRepo.findAll().stream()
                    .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                    .collect(Collectors.toList());

            double ramas = necesar;

            for (Stock s : ingredienteStock) {
                if (ramas <= 0) break;

                double deScazut = Math.min((double)s.getCantitate(), ramas);

                // Fixed the subtraction logic and maintained your (int) cast
                s.setCantitate((int)(s.getCantitate() - deScazut));
                ramas -= deScazut;

                stocRepo.update(s);
            }
        }
    }
}