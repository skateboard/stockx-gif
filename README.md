<h1 align="center">StockX Gif Converter</h1>
<div align="center">
	<strong>make stockx images look cool</strong>
</div>
<br />

# Idea
I got this idea from [Fyko/stockx-gif-next](https://github.com/Fyko/stockx-gif-next)

# Usage
```java
import me.brennan.stockxgiffer.StockXGiffer;

public class Test {

    public static void main(String[] args) throws Exception {
        final StockXGiffer stockXGiffer = new StockXGiffer("https://stockx.com/adidas-yeezy-boost-350-v2-desert-sage", "output");
        stockXGiffer.perform();
    }

}
```

# Example response
![](showcase.gif)
