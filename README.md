# Demo Application For e-commerce

Uygulama içerisinde bulunan model tanımları:

`Product, Category, Campaign, Coupon` 

Her Product bir Category'e ait olmak zorunda.
Her Category başka bir Category'nin altında olabilir de olmayabilir de.

Campaign ve Coupon tanımlamaları yapılabilir. Ürünlerin toplam fiyatına göre indirm sağlamak için kullanılır.
Her iki indirim türü bellirli bir oran ya da bellirli bir fiyat miktarında indirim sağlayabilir.
Campaing indirimleri belirli bir Category seviyesinde tanımlanabilir. 
Campaign indirimleri toplam ürünler göz önünde alınarak max. indirim miktarını sağlayacak şekilde kullandırılır.
Coupon ise alışveriş sonunda toplam fiyat üzerinden indirim sağlamak için kullanılabilir.


**Category tanımlaması**  
`Category giyim = new Category("Giyim");`       
`Category kazakHirka = new Category(giyim, "Kazak&Hırka");`       
`Category kazak = new Category(kazakHirka, "Kazak");`       
`Category hirka = new Category(kazakHirka, "Hırka");`   
Category'nin ait olduğu tüm üst dizinleri `.getDescription()` metodu ile gösterilir.



**Product tanımlaması**  
`Product kazak1 = new Product("Defacto Erkek Kazak", 39.99, kazak)`       
`Product hirka1 = new Product("Defacto Erkek Hırka", 49.99, hirka)`       
Product'ın ait olduğu tüm Category'i `.getDescription()` metodu ile gösterilir.


**Campaign tanımlaması**  
giyim kategorisinde en az 2 ürün alınırsa %25 indirim uygulanır: `Campaign campaign1 = new Campaign(giyim, 25.0, 2, DiscountType.RATE);`

kazak kategorisinde en az 3 ürün alınırsa 25 TL indirim uygulanır: `Campaign campaign2 = new Campaign(kazak, 25.0, 3, DiscountType.PRICE);`

**Coupon tanımlaması**    
min 100 TL alışverişe %10 indirim: `Coupon coupon1 = new Coupon(100.0, 10.0, DiscountType.RATE);`  
min 500 TL alışverişe 50 indirim: `Coupon coupon1 = new Coupon(100.0, 50.0, DiscountType.PRICE);`


**Alışveriş İşlemleri**    

Sepet oluşturmak için: `ShoppingCart cart = new ShoppingCart();`   
Sepete 2 adet kazak eklemek için: `cart.addItem(kazak1, 2);`
Sepete 1 adet hırka eklemek için: `cart.addItem(hirka1, 1);`   

kampanyaları alışverişe uygulamak için:`cart.applyDiscount(campaign1, campaign2, campaign3);`   
kuponları alşverişe uygulamak için: `cart.applyCoupon(coupon1); cart.applyCoupon(coupon2);`   

Kargo maaliyetini hesaplamak için:  
`DeliveryCostUtils delivery = new DeliveryCostUtils(CostPerDelivery, CostPerProduct, FixedCost);`   
`delivery.calculateFor(cart);`  

MAaliyet hesaplama tamamen kullanıcıya bırakıldı, yeni bir ürün eklenirse kargo maaliyeti tekrar hesaplatılmalıdır. 
Mmaaliyet hesaplanırken uygulana formül:`(CostPerDelivery*NumberOfDeliveries)+(CostPerProduct*NumberOfProducts)+FixedCost`

alışveriş detaylarının listelenmesi için: `cart.print();`   
`cart.getCost()` kargo ve indirimler dahil toplam tutarı gösterir. 

alışveriş için her eklenen ürün `productList` isimli değişkende miktarı ile birlikte tutulur.
kampanyalar `campaignList`, kuponlar `couponList` değişkenlerinde tutulur.
İndirim tutarı her yeni ürün eklendiğinde tekrar hesaplanır. 
kampanya indirmleri eklenirken kategorisine uygun tüm ürünlere göre sağladığı indirim tutarı hesaplanır ve campaignList değişkenine eklenir.
kampanya indirimleri arasında aynı kategoriye veya birbirini kapsayan kategorilere ait olan kampanyalar arasından en çok indirim miktarını sağlayan kampanya tespit edilip `applicableCampaignList` değişkenine eklenir.
Bu işlem CampaignCostUtils.getApplicableCampaigns() metodunda gerçekleştirilir.

ürünlerin toplam maaliyeti hesaplandıktan sonra, kampanya indirimleri uygulanır. 
Kampanya indirimlerinden sonra kupon indirimleri, indirim şartları sağlanıyorsa uygulanır.
kuponlar max. indirim sağladığı tutara göre uygulanır.


