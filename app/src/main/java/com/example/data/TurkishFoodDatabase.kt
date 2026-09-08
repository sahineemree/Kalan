package com.example.data

object TurkishFoodDatabase {
    val initialFoods = listOf(
        // KAHVALTILIK & SÜT ÜRÜNLERİ
        FoodItem(name = "Menemen", category = "Kahvaltılık", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 95.0, carbsPer100g = 3.5, proteinPer100g = 4.8, fatPer100g = 7.0, fiberPer100g = 1.2),
        FoodItem(name = "Haşlanmış Yumurta", category = "Kahvaltılık", servingName = "Adet (Büyük)", servingGrams = 55.0, caloriesPer100g = 155.0, carbsPer100g = 1.1, proteinPer100g = 12.6, fatPer100g = 10.6, fiberPer100g = 0.0),
        FoodItem(name = "Sahanda Yumurta (Tereyağlı)", category = "Kahvaltılık", servingName = "Porsiyon (2 Yumurta)", servingGrams = 120.0, caloriesPer100g = 195.0, carbsPer100g = 0.8, proteinPer100g = 11.5, fatPer100g = 16.5, fiberPer100g = 0.0),
        FoodItem(name = "Sucuklu Yumurta", category = "Kahvaltılık", servingName = "Porsiyon", servingGrams = 150.0, caloriesPer100g = 240.0, carbsPer100g = 1.5, proteinPer100g = 14.0, fatPer100g = 20.0, fiberPer100g = 0.1),
        FoodItem(name = "Beyaz Peynir (Tam Yağlı)", category = "Kahvaltılık", servingName = "Dilim (Kibrit Kutusu)", servingGrams = 30.0, caloriesPer100g = 260.0, carbsPer100g = 2.0, proteinPer100g = 16.0, fatPer100g = 21.0, fiberPer100g = 0.0),
        FoodItem(name = "Kaşar Peyniri (Eski)", category = "Kahvaltılık", servingName = "Dilim", servingGrams = 25.0, caloriesPer100g = 350.0, carbsPer100g = 1.5, proteinPer100g = 27.0, fatPer100g = 26.0, fiberPer100g = 0.0),
        FoodItem(name = "Lor Peyniri (Yağsız)", category = "Kahvaltılık", servingName = "Yemek Kaşığı", servingGrams = 30.0, caloriesPer100g = 85.0, carbsPer100g = 3.0, proteinPer100g = 17.0, fatPer100g = 1.0, fiberPer100g = 0.0),
        FoodItem(name = "Tulum Peyniri", category = "Kahvaltılık", servingName = "Porsiyon", servingGrams = 30.0, caloriesPer100g = 330.0, carbsPer100g = 1.8, proteinPer100g = 22.0, fatPer100g = 26.0, fiberPer100g = 0.0),
        FoodItem(name = "Siyah Zeytin (Gemlik)", category = "Kahvaltılık", servingName = "Adet (5 adet)", servingGrams = 20.0, caloriesPer100g = 207.0, carbsPer100g = 6.0, proteinPer100g = 1.8, fatPer100g = 21.0, fiberPer100g = 3.2),
        FoodItem(name = "Yeşil Kırma Zeytin", category = "Kahvaltılık", servingName = "Adet (5 adet)", servingGrams = 20.0, caloriesPer100g = 145.0, carbsPer100g = 3.8, proteinPer100g = 1.4, fatPer100g = 15.0, fiberPer100g = 3.3),
        FoodItem(name = "Yulaf Ezmesi", category = "Kahvaltılık", servingName = "Porsiyon (4 Kaşık)", servingGrams = 40.0, caloriesPer100g = 370.0, carbsPer100g = 60.0, proteinPer100g = 12.5, fatPer100g = 7.0, fiberPer100g = 10.0),
        FoodItem(name = "Süzme Yoğurt", category = "Kahvaltılık", servingName = "Kase", servingGrams = 150.0, caloriesPer100g = 115.0, carbsPer100g = 4.5, proteinPer100g = 8.5, fatPer100g = 7.0, fiberPer100g = 0.0),
        FoodItem(name = "Köy Tereyağı", category = "Kahvaltılık", servingName = "Tatlı Kaşığı", servingGrams = 10.0, caloriesPer100g = 720.0, carbsPer100g = 0.6, proteinPer100g = 0.8, fatPer100g = 81.0, fiberPer100g = 0.0),
        FoodItem(name = "Süzme Çiçek Balı", category = "Kahvaltılık", servingName = "Tatlı Kaşığı", servingGrams = 15.0, caloriesPer100g = 304.0, carbsPer100g = 82.0, proteinPer100g = 0.3, fatPer100g = 0.0, fiberPer100g = 0.2),
        FoodItem(name = "Tahin & Pekmez Karışımı", category = "Kahvaltılık", servingName = "Yemek Kaşığı", servingGrams = 20.0, caloriesPer100g = 420.0, carbsPer100g = 45.0, proteinPer100g = 9.0, fatPer100g = 24.0, fiberPer100g = 3.0),
        FoodItem(name = "Manda Kaymağı", category = "Kahvaltılık", servingName = "Tatlı Kaşığı", servingGrams = 20.0, caloriesPer100g = 585.0, carbsPer100g = 2.0, proteinPer100g = 1.5, fatPer100g = 63.0, fiberPer100g = 0.0),

        // EKMEK & HAMUR İŞLERİ
        FoodItem(name = "Tam Buğday Ekmeği", category = "Ekmek & Hamur İşi", servingName = "Dilim", servingGrams = 30.0, caloriesPer100g = 245.0, carbsPer100g = 45.0, proteinPer100g = 9.5, fatPer100g = 2.5, fiberPer100g = 6.8),
        FoodItem(name = "Beyaz Ekmek (Somun)", category = "Ekmek & Hamur İşi", servingName = "Dilim", servingGrams = 30.0, caloriesPer100g = 265.0, carbsPer100g = 52.0, proteinPer100g = 8.0, fatPer100g = 1.5, fiberPer100g = 2.5),
        FoodItem(name = "Çavdar Ekmeği", category = "Ekmek & Hamur İşi", servingName = "Dilim", servingGrams = 30.0, caloriesPer100g = 230.0, carbsPer100g = 44.0, proteinPer100g = 8.5, fatPer100g = 1.8, fiberPer100g = 5.8),
        FoodItem(name = "Susamlı Simit", category = "Ekmek & Hamur İşi", servingName = "Adet", servingGrams = 100.0, caloriesPer100g = 320.0, carbsPer100g = 58.0, proteinPer100g = 10.0, fatPer100g = 5.5, fiberPer100g = 3.5),
        FoodItem(name = "Peynirli Poğaça", category = "Ekmek & Hamur İşi", servingName = "Adet", servingGrams = 80.0, caloriesPer100g = 340.0, carbsPer100g = 40.0, proteinPer100g = 8.5, fatPer100g = 16.5, fiberPer100g = 1.8),
        FoodItem(name = "Sade Açma", category = "Ekmek & Hamur İşi", servingName = "Adet", servingGrams = 90.0, caloriesPer100g = 370.0, carbsPer100g = 45.0, proteinPer100g = 7.5, fatPer100g = 18.0, fiberPer100g = 2.0),
        FoodItem(name = "Kıymalı Su Böreği", category = "Ekmek & Hamur İşi", servingName = "Dilim", servingGrams = 150.0, caloriesPer100g = 260.0, carbsPer100g = 26.0, proteinPer100g = 10.5, fatPer100g = 13.0, fiberPer100g = 1.5),
        FoodItem(name = "Peynirli Sigara Böreği", category = "Ekmek & Hamur İşi", servingName = "Adet (3 adet)", servingGrams = 90.0, caloriesPer100g = 310.0, carbsPer100g = 32.0, proteinPer100g = 9.0, fatPer100g = 16.0, fiberPer100g = 1.2),
        FoodItem(name = "Ispanaklı Peynirli Gözleme", category = "Ekmek & Hamur İşi", servingName = "Adet", servingGrams = 180.0, caloriesPer100g = 215.0, carbsPer100g = 32.0, proteinPer100g = 7.5, fatPer100g = 6.5, fiberPer100g = 2.4),
        FoodItem(name = "Kıymalı Pide", category = "Ekmek & Hamur İşi", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 245.0, carbsPer100g = 34.0, proteinPer100g = 11.0, fatPer100g = 7.5, fiberPer100g = 2.2),
        FoodItem(name = "Kuşbaşılı Kaşarlı Pide", category = "Ekmek & Hamur İşi", servingName = "Porsiyon", servingGrams = 220.0, caloriesPer100g = 265.0, carbsPer100g = 33.0, proteinPer100g = 13.5, fatPer100g = 9.5, fiberPer100g = 2.0),
        FoodItem(name = "Lavaş Ekmeği", category = "Ekmek & Hamur İşi", servingName = "Adet", servingGrams = 60.0, caloriesPer100g = 270.0, carbsPer100g = 53.0, proteinPer100g = 8.5, fatPer100g = 2.5, fiberPer100g = 3.0),

        // ÇORBALAR
        FoodItem(name = "Kırmızı Mercimek Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 55.0, carbsPer100g = 8.5, proteinPer100g = 3.2, fatPer100g = 1.2, fiberPer100g = 1.8),
        FoodItem(name = "Ezogelin Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 60.0, carbsPer100g = 9.2, proteinPer100g = 2.8, fatPer100g = 1.5, fiberPer100g = 1.5),
        FoodItem(name = "Yayla Çorbası (Yoğurtlu)", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 50.0, carbsPer100g = 6.5, proteinPer100g = 2.2, fatPer100g = 1.8, fiberPer100g = 0.5),
        FoodItem(name = "Tarhana Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 62.0, carbsPer100g = 9.8, proteinPer100g = 2.4, fatPer100g = 1.6, fiberPer100g = 1.2),
        FoodItem(name = "Köz Domates Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 45.0, carbsPer100g = 7.0, proteinPer100g = 1.5, fatPer100g = 1.4, fiberPer100g = 1.1),
        FoodItem(name = "Tavuk Suyu Şehriye Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 48.0, carbsPer100g = 5.5, proteinPer100g = 3.5, fatPer100g = 1.4, fiberPer100g = 0.4),
        FoodItem(name = "Kelle Paça Çorbası (Sarımsaklı)", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 135.0, carbsPer100g = 1.2, proteinPer100g = 12.0, fatPer100g = 9.5, fiberPer100g = 0.1),
        FoodItem(name = "İşkembe Çorbası", category = "Çorbalar", servingName = "Kase", servingGrams = 250.0, caloriesPer100g = 110.0, carbsPer100g = 2.0, proteinPer100g = 10.0, fatPer100g = 7.0, fiberPer100g = 0.0),

        // ANA YEMEKLER (ET, TAVUK, KEBAP)
        FoodItem(name = "Tavuklu Pirinç Pilavı", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 175.0, carbsPer100g = 24.0, proteinPer100g = 9.5, fatPer100g = 4.5, fiberPer100g = 0.8),
        FoodItem(name = "Izgara Tavuk Göğsü", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 130.0, carbsPer100g = 0.0, proteinPer100g = 27.0, fatPer100g = 2.5, fiberPer100g = 0.0),
        FoodItem(name = "Sebzeli Tavuk Sote", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 115.0, carbsPer100g = 4.0, proteinPer100g = 15.0, fatPer100g = 4.5, fiberPer100g = 1.2),
        FoodItem(name = "Izgara Dana Köfte", category = "Ana Yemekler", servingName = "Porsiyon (4-5 köfte)", servingGrams = 160.0, caloriesPer100g = 220.0, carbsPer100g = 5.0, proteinPer100g = 18.0, fatPer100g = 14.0, fiberPer100g = 0.5),
        FoodItem(name = "İskender Kebap", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 300.0, caloriesPer100g = 210.0, carbsPer100g = 14.0, proteinPer100g = 13.0, fatPer100g = 12.0, fiberPer100g = 1.2),
        FoodItem(name = "Adana Kebap (Lavaşsız)", category = "Ana Yemekler", servingName = "Porsiyon (Şiş)", servingGrams = 150.0, caloriesPer100g = 245.0, carbsPer100g = 2.0, proteinPer100g = 19.0, fatPer100g = 18.0, fiberPer100g = 0.8),
        FoodItem(name = "Urfa Kebap", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 150.0, caloriesPer100g = 240.0, carbsPer100g = 1.8, proteinPer100g = 19.5, fatPer100g = 17.5, fiberPer100g = 0.5),
        FoodItem(name = "Karnıyarık (Kıymalı Patlıcan)", category = "Ana Yemekler", servingName = "Adet", servingGrams = 200.0, caloriesPer100g = 125.0, carbsPer100g = 5.5, proteinPer100g = 6.5, fatPer100g = 9.0, fiberPer100g = 2.5),
        FoodItem(name = "İmam Bayıldı (Zeytinyağlı)", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 95.0, carbsPer100g = 7.0, proteinPer100g = 2.0, fatPer100g = 7.0, fiberPer100g = 3.0),
        FoodItem(name = "Hünkar Beğendi", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 185.0, carbsPer100g = 6.5, proteinPer100g = 14.0, fatPer100g = 12.0, fiberPer100g = 2.0),
        FoodItem(name = "Tas Kebabı (Patatesli)", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 220.0, caloriesPer100g = 155.0, carbsPer100g = 7.5, proteinPer100g = 14.5, fatPer100g = 7.8, fiberPer100g = 1.3),
        FoodItem(name = "Fırında Somon Izgara", category = "Ana Yemekler", servingName = "Fileto", servingGrams = 180.0, caloriesPer100g = 180.0, carbsPer100g = 0.0, proteinPer100g = 22.0, fatPer100g = 10.0, fiberPer100g = 0.0),
        FoodItem(name = "Izgara Levrek", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 110.0, carbsPer100g = 0.0, proteinPer100g = 21.0, fatPer100g = 2.8, fiberPer100g = 0.0),
        FoodItem(name = "Hamsi Tava (Mısır Unlu)", category = "Ana Yemekler", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 210.0, carbsPer100g = 8.5, proteinPer100g = 18.0, fatPer100g = 12.0, fiberPer100g = 0.6),

        // SEBZE, BAKLİYAT & ZEYTİNYAĞLILAR
        FoodItem(name = "Etli Kuru Fasulye", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 135.0, carbsPer100g = 15.0, proteinPer100g = 8.5, fatPer100g = 4.8, fiberPer100g = 5.2),
        FoodItem(name = "Etli Nohut Yemeği", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 145.0, carbsPer100g = 16.5, proteinPer100g = 9.0, fatPer100g = 5.0, fiberPer100g = 4.8),
        FoodItem(name = "Zeytinyağlı Taze Fasulye", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 70.0, carbsPer100g = 6.0, proteinPer100g = 2.0, fatPer100g = 4.2, fiberPer100g = 2.8),
        FoodItem(name = "Kıymalı Biber Dolması", category = "Sebze & Bakliyat", servingName = "Adet (2 Dolma)", servingGrams = 220.0, caloriesPer100g = 120.0, carbsPer100g = 11.0, proteinPer100g = 5.5, fatPer100g = 6.0, fiberPer100g = 2.1),
        FoodItem(name = "Zeytinyağlı Yaprak Sarması", category = "Sebze & Bakliyat", servingName = "Porsiyon (5 Sarma)", servingGrams = 150.0, caloriesPer100g = 165.0, carbsPer100g = 21.0, proteinPer100g = 3.0, fatPer100g = 8.0, fiberPer100g = 2.6),
        FoodItem(name = "Zeytinyağlı Pırasa", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 65.0, carbsPer100g = 7.5, proteinPer100g = 1.6, fatPer100g = 3.5, fiberPer100g = 2.2),
        FoodItem(name = "Zeytinyağlı Enginar", category = "Sebze & Bakliyat", servingName = "Adet", servingGrams = 150.0, caloriesPer100g = 75.0, carbsPer100g = 8.0, proteinPer100g = 2.5, fatPer100g = 3.8, fiberPer100g = 4.5),
        FoodItem(name = "Kıymalı Bezelye Yemeği", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 115.0, carbsPer100g = 10.5, proteinPer100g = 6.5, fatPer100g = 5.5, fiberPer100g = 3.5),
        FoodItem(name = "Yeşil Mercimek Yemeği", category = "Sebze & Bakliyat", servingName = "Porsiyon", servingGrams = 220.0, caloriesPer100g = 110.0, carbsPer100g = 14.5, proteinPer100g = 6.8, fatPer100g = 2.8, fiberPer100g = 4.8),

        // PİLAV & MAKARNALAR
        FoodItem(name = "Tereyağlı Şehriyeli Pirinç Pilavı", category = "Pilav & Makarna", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 185.0, carbsPer100g = 30.0, proteinPer100g = 3.2, fatPer100g = 6.0, fiberPer100g = 0.8),
        FoodItem(name = "Meyhane Bulgur Pilavı", category = "Pilav & Makarna", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 140.0, carbsPer100g = 23.0, proteinPer100g = 4.5, fatPer100g = 3.8, fiberPer100g = 4.5),
        FoodItem(name = "Domates Soslu Makarna", category = "Pilav & Makarna", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 135.0, carbsPer100g = 25.0, proteinPer100g = 4.2, fatPer100g = 2.2, fiberPer100g = 1.8),
        FoodItem(name = "Kıymalı Soslu Spagetti", category = "Pilav & Makarna", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 160.0, carbsPer100g = 22.0, proteinPer100g = 7.5, fatPer100g = 5.0, fiberPer100g = 1.7),
        FoodItem(name = "Kayseri Mantısı (Yoğurtlu & Tereyağlı)", category = "Pilav & Makarna", servingName = "Porsiyon", servingGrams = 250.0, caloriesPer100g = 180.0, carbsPer100g = 24.0, proteinPer100g = 7.8, fatPer100g = 6.2, fiberPer100g = 1.2),

        // SOKAK LEZZETLERİ & FAST FOOD
        FoodItem(name = "Lahmacun (Çıtır)", category = "Sokak Lezzeti", servingName = "Adet", servingGrams = 140.0, caloriesPer100g = 175.0, carbsPer100g = 25.0, proteinPer100g = 7.8, fatPer100g = 5.2, fiberPer100g = 1.8),
        FoodItem(name = "Et Döner Dürüm", category = "Sokak Lezzeti", servingName = "Porsiyon", servingGrams = 220.0, caloriesPer100g = 215.0, carbsPer100g = 22.0, proteinPer100g = 14.0, fatPer100g = 8.0, fiberPer100g = 1.5),
        FoodItem(name = "Tavuk Döner Dürüm", category = "Sokak Lezzeti", servingName = "Porsiyon", servingGrams = 220.0, caloriesPer100g = 195.0, carbsPer100g = 23.0, proteinPer100g = 13.0, fatPer100g = 6.0, fiberPer100g = 1.4),
        FoodItem(name = "Tantuni (Lavaş Dürüm)", category = "Sokak Lezzeti", servingName = "Adet", servingGrams = 180.0, caloriesPer100g = 225.0, carbsPer100g = 21.0, proteinPer100g = 15.0, fatPer100g = 9.0, fiberPer100g = 1.3),
        FoodItem(name = "Çiğ Köfte Dürüm", category = "Sokak Lezzeti", servingName = "Dürüm", servingGrams = 180.0, caloriesPer100g = 185.0, carbsPer100g = 32.0, proteinPer100g = 5.2, fatPer100g = 4.2, fiberPer100g = 4.5),
        FoodItem(name = "Kokoreç (Yarım Ekmek)", category = "Sokak Lezzeti", servingName = "Yarım Ekmek", servingGrams = 220.0, caloriesPer100g = 255.0, carbsPer100g = 26.0, proteinPer100g = 12.5, fatPer100g = 11.5, fiberPer100g = 1.5),
        FoodItem(name = "Midye Dolma", category = "Sokak Lezzeti", servingName = "Adet (5 adet)", servingGrams = 100.0, caloriesPer100g = 140.0, carbsPer100g = 19.0, proteinPer100g = 5.0, fatPer100g = 4.8, fiberPer100g = 0.8),
        FoodItem(name = "İzmir Kumru", category = "Sokak Lezzeti", servingName = "Adet", servingGrams = 200.0, caloriesPer100g = 295.0, carbsPer100g = 28.0, proteinPer100g = 13.5, fatPer100g = 14.5, fiberPer100g = 1.2),

        // SALATA & MEZELER
        FoodItem(name = "Geleneksel Çoban Salata", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 200.0, caloriesPer100g = 45.0, carbsPer100g = 4.5, proteinPer100g = 1.2, fatPer100g = 2.5, fiberPer100g = 1.5),
        FoodItem(name = "Mevsim Yeşillikleri Salatası", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 38.0, carbsPer100g = 3.2, proteinPer100g = 1.4, fatPer100g = 2.2, fiberPer100g = 1.8),
        FoodItem(name = "Klasik Kısır", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 150.0, caloriesPer100g = 175.0, carbsPer100g = 27.0, proteinPer100g = 4.2, fatPer100g = 6.0, fiberPer100g = 4.0),
        FoodItem(name = "Mercimek Köftesi", category = "Salata & Meze", servingName = "Adet (4 adet)", servingGrams = 120.0, caloriesPer100g = 160.0, carbsPer100g = 24.0, proteinPer100g = 6.8, fatPer100g = 4.5, fiberPer100g = 4.2),
        FoodItem(name = "Cacık (Salatalıklı & Naneli)", category = "Salata & Meze", servingName = "Kase", servingGrams = 200.0, caloriesPer100g = 55.0, carbsPer100g = 4.0, proteinPer100g = 3.5, fatPer100g = 2.8, fiberPer100g = 0.5),
        FoodItem(name = "Haydari", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 100.0, caloriesPer100g = 135.0, carbsPer100g = 5.0, proteinPer100g = 6.5, fatPer100g = 10.0, fiberPer100g = 0.2),
        FoodItem(name = "Humus (Tahinli & Zeytinyağlı)", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 100.0, caloriesPer100g = 195.0, carbsPer100g = 16.0, proteinPer100g = 7.0, fatPer100g = 11.5, fiberPer100g = 5.5),
        FoodItem(name = "Şakşuka", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 150.0, caloriesPer100g = 95.0, carbsPer100g = 8.5, proteinPer100g = 1.8, fatPer100g = 6.0, fiberPer100g = 2.4),
        FoodItem(name = "Antalya Usulü Piyaz", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 155.0, carbsPer100g = 17.0, proteinPer100g = 7.5, fatPer100g = 6.5, fiberPer100g = 4.2),
        FoodItem(name = "Acılı Ezme", category = "Salata & Meze", servingName = "Porsiyon", servingGrams = 100.0, caloriesPer100g = 60.0, carbsPer100g = 6.5, proteinPer100g = 1.5, fatPer100g = 3.2, fiberPer100g = 2.0),

        // TATLILAR & ATIŞTIRMALIKLAR
        FoodItem(name = "Fırın Sütlaç", category = "Tatlılar", servingName = "Kase", servingGrams = 200.0, caloriesPer100g = 135.0, carbsPer100g = 24.0, proteinPer100g = 3.5, fatPer100g = 2.8, fiberPer100g = 0.2),
        FoodItem(name = "Kazandibi", category = "Tatlılar", servingName = "Porsiyon", servingGrams = 180.0, caloriesPer100g = 145.0, carbsPer100g = 26.0, proteinPer100g = 3.8, fatPer100g = 3.0, fiberPer100g = 0.2),
        FoodItem(name = "Cevizli Ev Baklavası", category = "Tatlılar", servingName = "Dilim (2 dilim)", servingGrams = 80.0, caloriesPer100g = 425.0, carbsPer100g = 55.0, proteinPer100g = 6.0, fatPer100g = 20.0, fiberPer100g = 2.5),
        FoodItem(name = "Fıstıklı Kadayıf", category = "Tatlılar", servingName = "Porsiyon", servingGrams = 120.0, caloriesPer100g = 390.0, carbsPer100g = 58.0, proteinPer100g = 7.0, fatPer100g = 15.0, fiberPer100g = 2.2),
        FoodItem(name = "Trileçe (Karamelli)", category = "Tatlılar", servingName = "Dilim", servingGrams = 150.0, caloriesPer100g = 210.0, carbsPer100g = 30.0, proteinPer100g = 5.2, fatPer100g = 8.0, fiberPer100g = 0.4),
        FoodItem(name = "Geleneksel Aşure", category = "Tatlılar", servingName = "Kase", servingGrams = 200.0, caloriesPer100g = 165.0, carbsPer100g = 33.0, proteinPer100g = 3.5, fatPer100g = 2.2, fiberPer100g = 3.5),
        FoodItem(name = "İrmik Helvası", category = "Tatlılar", servingName = "Porsiyon", servingGrams = 120.0, caloriesPer100g = 320.0, carbsPer100g = 48.0, proteinPer100g = 4.5, fatPer100g = 12.5, fiberPer100g = 1.4),
        FoodItem(name = "Bitter Çikolata (%70 Kakaolu)", category = "Tatlılar", servingName = "Kare (4 kare)", servingGrams = 25.0, caloriesPer100g = 560.0, carbsPer100g = 36.0, proteinPer100g = 8.0, fatPer100g = 42.0, fiberPer100g = 11.0),

        // MEYVELER & KURUYEMİŞ
        FoodItem(name = "Kırmızı Elma", category = "Meyve & Kuruyemiş", servingName = "Adet (Orta)", servingGrams = 150.0, caloriesPer100g = 52.0, carbsPer100g = 13.8, proteinPer100g = 0.3, fatPer100g = 0.2, fiberPer100g = 2.4),
        FoodItem(name = "Yerli Muz", category = "Meyve & Kuruyemiş", servingName = "Adet (Orta)", servingGrams = 120.0, caloriesPer100g = 89.0, carbsPer100g = 22.8, proteinPer100g = 1.1, fatPer100g = 0.3, fiberPer100g = 2.6),
        FoodItem(name = "Portakal", category = "Meyve & Kuruyemiş", servingName = "Adet", servingGrams = 160.0, caloriesPer100g = 47.0, carbsPer100g = 11.8, proteinPer100g = 0.9, fatPer100g = 0.1, fiberPer100g = 2.4),
        FoodItem(name = "Çilek", category = "Meyve & Kuruyemiş", servingName = "Kase", servingGrams = 150.0, caloriesPer100g = 32.0, carbsPer100g = 7.7, proteinPer100g = 0.7, fatPer100g = 0.3, fiberPer100g = 2.0),
        FoodItem(name = "Karpuz", category = "Meyve & Kuruyemiş", servingName = "Dilim", servingGrams = 200.0, caloriesPer100g = 30.0, carbsPer100g = 7.6, proteinPer100g = 0.6, fatPer100g = 0.2, fiberPer100g = 0.4),
        FoodItem(name = "Kavun", category = "Meyve & Kuruyemiş", servingName = "Dilim", servingGrams = 200.0, caloriesPer100g = 34.0, carbsPer100g = 8.2, proteinPer100g = 0.8, fatPer100g = 0.2, fiberPer100g = 0.9),
        FoodItem(name = "Ceviz İçi", category = "Meyve & Kuruyemiş", servingName = "Avuç (3-4 ceviz)", servingGrams = 30.0, caloriesPer100g = 654.0, carbsPer100g = 13.7, proteinPer100g = 15.2, fatPer100g = 65.2, fiberPer100g = 6.7),
        FoodItem(name = "Çiğ Badem", category = "Meyve & Kuruyemiş", servingName = "Avuç (15-20 badem)", servingGrams = 30.0, caloriesPer100g = 579.0, carbsPer100g = 21.6, proteinPer100g = 21.2, fatPer100g = 49.9, fiberPer100g = 12.5),
        FoodItem(name = "Kavrulmuş Fındık", category = "Meyve & Kuruyemiş", servingName = "Avuç", servingGrams = 30.0, caloriesPer100g = 628.0, carbsPer100g = 16.7, proteinPer100g = 15.0, fatPer100g = 60.8, fiberPer100g = 9.7),
        FoodItem(name = "Antep Fıstığı", category = "Meyve & Kuruyemiş", servingName = "Avuç", servingGrams = 30.0, caloriesPer100g = 560.0, carbsPer100g = 27.5, proteinPer100g = 20.0, fatPer100g = 45.3, fiberPer100g = 10.6),
        FoodItem(name = "Kuru Günkurusu Kayısı", category = "Meyve & Kuruyemiş", servingName = "Adet (3 adet)", servingGrams = 30.0, caloriesPer100g = 241.0, carbsPer100g = 62.6, proteinPer100g = 3.4, fatPer100g = 0.5, fiberPer100g = 7.3),
        FoodItem(name = "Kuru İncir", category = "Meyve & Kuruyemiş", servingName = "Adet (2 adet)", servingGrams = 40.0, caloriesPer100g = 249.0, carbsPer100g = 63.9, proteinPer100g = 3.3, fatPer100g = 0.9, fiberPer100g = 9.8),

        // İÇECEKLER
        FoodItem(name = "Geleneksel Yayık Ayran", category = "İçecekler", servingName = "Su Bardağı", servingGrams = 200.0, caloriesPer100g = 38.0, carbsPer100g = 2.8, proteinPer100g = 2.0, fatPer100g = 2.0, fiberPer100g = 0.0),
        FoodItem(name = "Sade Türk Kahvesi", category = "İçecekler", servingName = "Fincan", servingGrams = 70.0, caloriesPer100g = 2.0, carbsPer100g = 0.2, proteinPer100g = 0.1, fatPer100g = 0.0, fiberPer100g = 0.0),
        FoodItem(name = "Demleme Siyah Çay (Şekersiz)", category = "İçecekler", servingName = "İnce Belli Bardak", servingGrams = 100.0, caloriesPer100g = 1.0, carbsPer100g = 0.2, proteinPer100g = 0.0, fatPer100g = 0.0, fiberPer100g = 0.0),
        FoodItem(name = "Sade Maden Suyu", category = "İçecekler", servingName = "Şişe", servingGrams = 200.0, caloriesPer100g = 0.0, carbsPer100g = 0.0, proteinPer100g = 0.0, fatPer100g = 0.0, fiberPer100g = 0.0),
        FoodItem(name = "Kefir (Sade)", category = "İçecekler", servingName = "Su Bardağı", servingGrams = 200.0, caloriesPer100g = 52.0, carbsPer100g = 4.0, proteinPer100g = 3.2, fatPer100g = 2.5, fiberPer100g = 0.0),
        FoodItem(name = "Taze Sıkma Portakal Suyu", category = "İçecekler", servingName = "Su Bardağı", servingGrams = 200.0, caloriesPer100g = 45.0, carbsPer100g = 10.4, proteinPer100g = 0.7, fatPer100g = 0.2, fiberPer100g = 0.2),
        FoodItem(name = "Ev Yapımı Nane Limonata (Az Şekerli)", category = "İçecekler", servingName = "Bardak", servingGrams = 250.0, caloriesPer100g = 30.0, carbsPer100g = 7.5, proteinPer100g = 0.2, fatPer100g = 0.0, fiberPer100g = 0.1)
    )
}
