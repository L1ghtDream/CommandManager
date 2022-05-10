# API
```java
public class ExampleClass {
    public void example(Player player){
        GemsUser user = GemsUser.of(player);
        boolean condition = user.hasGems(100);
        int amount = user.getGems();
        user.giveGems(1000);
        user.takeGems(1000);
        user.setGems(1000);
    }
}
```