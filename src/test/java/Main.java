import me.brennan.stockxgiffer.StockXGiffer;

/**
 * @author Brennan
 * @since 5/15/2021
 **/
public class Main {

    public static void main(String[] args) throws Exception {
        final StockXGiffer stockXGiffer = new StockXGiffer("https://stockx.com/adidas-yeezy-boost-350-v2-desert-sage", "test");
        stockXGiffer.perform();
    }

}
