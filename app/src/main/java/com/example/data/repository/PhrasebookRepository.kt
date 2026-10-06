package com.example.data.repository

import com.example.data.model.PhraseCategory
import com.example.data.model.PhraseItem

object PhrasebookRepository {

    val allPhrases: List<PhraseItem> = listOf(
        // Greetings & Essentials
        PhraseItem(
            id = "g1",
            category = PhraseCategory.GREETINGS,
            english = "Hello / Welcome",
            turkish = "Merhaba / Hoş geldiniz",
            turkishPhonetic = "mer-ha-ba / hosh gel-dee-neez",
            arabic = "مرحباً / أهلاً وسهلاً",
            urdu = "ہیلو / خوش آمدید"
        ),
        PhraseItem(
            id = "g2",
            category = PhraseCategory.GREETINGS,
            english = "Good morning",
            turkish = "Günaydın",
            turkishPhonetic = "goo-nay-duhn",
            arabic = "صباح الخير",
            urdu = "صبح بخیر"
        ),
        PhraseItem(
            id = "g3",
            category = PhraseCategory.GREETINGS,
            english = "Good evening",
            turkish = "İyi akşamlar",
            turkishPhonetic = "ee-yee ak-sham-lar",
            arabic = "مساء الخير",
            urdu = "شام بخیر"
        ),
        PhraseItem(
            id = "g4",
            category = PhraseCategory.GREETINGS,
            english = "Thank you very much",
            turkish = "Çok teşekkür ederim",
            turkishPhonetic = "chok teh-shek-koor eh-deh-reem",
            arabic = "شكراً جزيلاً",
            urdu = "بہت بہت شکریہ"
        ),
        PhraseItem(
            id = "g5",
            category = PhraseCategory.GREETINGS,
            english = "You're welcome",
            turkish = "Rica ederim",
            turkishPhonetic = "ree-jah eh-deh-reem",
            arabic = "عفواً / على الرحب والسعة",
            urdu = "خوش آمدید / کوئی بات نہیں"
        ),
        PhraseItem(
            id = "g6",
            category = PhraseCategory.GREETINGS,
            english = "Please",
            turkish = "Lütfen",
            turkishPhonetic = "loot-fen",
            arabic = "من فضلك / رجاءً",
            urdu = "براہ کرم"
        ),
        PhraseItem(
            id = "g7",
            category = PhraseCategory.GREETINGS,
            english = "Excuse me / I'm sorry",
            turkish = "Afedersiniz / Özür dilerim",
            turkishPhonetic = "af-eh-dair-see-neez / oh-zoor dee-leh-reem",
            arabic = "لو سمحت / أنا آسف",
            urdu = "معاف کیجئے گا"
        ),
        PhraseItem(
            id = "g8",
            category = PhraseCategory.GREETINGS,
            english = "Goodbye",
            turkish = "Güle güle / Hoşça kalın",
            turkishPhonetic = "goo-leh goo-leh / hosh-cha ka-luhn",
            arabic = "مع السلامة / إلى اللقاء",
            urdu = "خدا حافظ"
        ),
        PhraseItem(
            id = "g9",
            category = PhraseCategory.GREETINGS,
            english = "Yes / No",
            turkish = "Evet / Hayır",
            turkishPhonetic = "eh-vet / ha-yuhr",
            arabic = "نعم / لا",
            urdu = "ہاں / نہیں"
        ),
        PhraseItem(
            id = "g10",
            category = PhraseCategory.GREETINGS,
            english = "Do you speak English?",
            turkish = "İngilizce biliyor musunuz?",
            turkishPhonetic = "een-gee-leez-jeh bee-lee-yor moo-soo-nooz?",
            arabic = "هل تتحدث الإنجليزية؟",
            urdu = "کیا آپ انگریزی بولتے ہیں؟"
        ),

        // Food & Dining
        PhraseItem(
            id = "d1",
            category = PhraseCategory.DINING,
            english = "The menu, please",
            turkish = "Menü alabilir miyim, lütfen?",
            turkishPhonetic = "meh-noo ah-lah-bee-leer mee-yeem loot-fen?",
            arabic = "قائمة الطعام من فضلك",
            urdu = "براہ کرم مینو دیجیے"
        ),
        PhraseItem(
            id = "d2",
            category = PhraseCategory.DINING,
            english = "Water, please",
            turkish = "Su lütfen",
            turkishPhonetic = "soo loot-fen",
            arabic = "ماء من فضلك",
            urdu = "براہ کرم پانی دیجیے"
        ),
        PhraseItem(
            id = "d3",
            category = PhraseCategory.DINING,
            english = "The bill / check, please",
            turkish = "Hesap lütfen",
            turkishPhonetic = "heh-sahp loot-fen",
            arabic = "الفاتورة من فضلك",
            urdu = "براہ کرم بل دیجیے"
        ),
        PhraseItem(
            id = "d4",
            category = PhraseCategory.DINING,
            english = "Is this food Halal?",
            turkish = "Bu yemek helal mi?",
            turkishPhonetic = "boo yeh-mek heh-lal mee?",
            arabic = "هل هذا الطعام حلال؟",
            urdu = "کیا یہ کھانا حلال ہے؟"
        ),
        PhraseItem(
            id = "d5",
            category = PhraseCategory.DINING,
            english = "Bon appétit / Enjoy your meal",
            turkish = "Afiyet olsun",
            turkishPhonetic = "ah-fee-yet ol-soon",
            arabic = "بالهناء والشفاء",
            urdu = "نوش جان / کھانا مبارک"
        ),
        PhraseItem(
            id = "d6",
            category = PhraseCategory.DINING,
            english = "I am a vegetarian",
            turkish = "Vejetaryenim",
            turkishPhonetic = "veh-zheh-tahr-yen-eem",
            arabic = "أنا نباتي",
            urdu = "میں سبزی خور ہوں"
        ),
        PhraseItem(
            id = "d7",
            category = PhraseCategory.DINING,
            english = "Turkish tea / Turkish coffee please",
            turkish = "Çay / Türk kahvesi lütfen",
            turkishPhonetic = "chay / toork kah-veh-see loot-fen",
            arabic = "شاي / قهوة تركية من فضلك",
            urdu = "ترکش چائے / ترکش کافی چاہیے"
        ),
        PhraseItem(
            id = "d8",
            category = PhraseCategory.DINING,
            english = "It was delicious, thank you",
            turkish = "Çok lezzetliydi, elinize sağlık",
            turkishPhonetic = "chok lez-zet-lee-dee, eh-lee-nee-zeh sah-luhk",
            arabic = "كان لذيذاً جداً، تسلم الأيادي",
            urdu = "بہت لذیذ تھا، شکریہ"
        ),

        // Transport & Directions
        PhraseItem(
            id = "t1",
            category = PhraseCategory.TRANSPORT,
            english = "Where is the tram / metro?",
            turkish = "Tramvay / Metro nerede?",
            turkishPhonetic = "tram-vay / meh-troh neh-reh-deh?",
            arabic = "أين محطة الترام / المترو؟",
            urdu = "ٹ crappyرام / میٹرو کہاں ہے؟"
        ),
        PhraseItem(
            id = "t2",
            category = PhraseCategory.TRANSPORT,
            english = "Take me to Taksim / Sultanahmet",
            turkish = "Beni Taksim'e / Sultanahmet'e götürün",
            turkishPhonetic = "beh-nee tak-see-meh / sool-tan-ah-meh-teh goh-too-roon",
            arabic = "خذني إلى تقسيم / السلطان أحمد",
            urdu = "مجھے تقسیم / سلطان احمد لے چلو"
        ),
        PhraseItem(
            id = "t3",
            category = PhraseCategory.TRANSPORT,
            english = "How much to the airport?",
            turkish = "Havalimanına ne kadar tutar?",
            turkishPhonetic = "ha-va-lee-mah-nuh-nah neh ka-dar too-tar?",
            arabic = "كم الأجرة إلى المطار؟",
            urdu = "ایئرپورٹ کا کتنا کرایہ ہوگا؟"
        ),
        PhraseItem(
            id = "t4",
            category = PhraseCategory.TRANSPORT,
            english = "Turn right / Turn left / Go straight",
            turkish = "Sağa dönün / Sola dönün / Düz gidin",
            turkishPhonetic = "sah-ah doh-noon / soh-lah doh-noon / dooz gee-deen",
            arabic = "انعطف يميناً / انعطف يساراً / سر للأمام",
            urdu = "دائیں مڑیں / بائیں مڑیں / سیدھے جائیں"
        ),
        PhraseItem(
            id = "t5",
            category = PhraseCategory.TRANSPORT,
            english = "Please stop here",
            turkish = "Burada durun lütfen",
            turkishPhonetic = "boo-rah-dah doo-roon loot-fen",
            arabic = "توقف هنا من فضلك",
            urdu = "براہ کرم یہاں روکیں"
        ),
        PhraseItem(
            id = "t6",
            category = PhraseCategory.TRANSPORT,
            english = "Where is the bathroom?",
            turkish = "Tuvalet nerede?",
            turkishPhonetic = "too-vah-let neh-reh-deh?",
            arabic = "أين دورة المياه؟",
            urdu = "واش روم کہاں ہے؟"
        ),

        // Shopping & Bazaars
        PhraseItem(
            id = "s1",
            category = PhraseCategory.SHOPPING,
            english = "How much is this?",
            turkish = "Bu ne kadar?",
            turkishPhonetic = "boo neh ka-dar?",
            arabic = "بكم هذا؟ / كم سعر هذا؟",
            urdu = "یہ کتنے کا ہے؟"
        ),
        PhraseItem(
            id = "s2",
            category = PhraseCategory.SHOPPING,
            english = "It's too expensive. Any discount?",
            turkish = "Çok pahalı. İndirim yapar mısınız?",
            turkishPhonetic = "chok pah-hah-luh. een-dee-reem yah-par muh-suh-nooz?",
            arabic = "هذا غالٍ جداً. هل يمكن تخفيض السعر؟",
            urdu = "یہ بہت مہنگا ہے۔ کچھ رعایت ہو سکتی ہے؟"
        ),
        PhraseItem(
            id = "s3",
            category = PhraseCategory.SHOPPING,
            english = "Do you accept credit cards?",
            turkish = "Kredi kartı kabul ediyor musunuz?",
            turkishPhonetic = "kreh-dee kar-tuh kah-bool eh-dee-yor moo-soo-nooz?",
            arabic = "هل تقبلون بطاقات الائتمان؟",
            urdu = "کیا آپ کریڈٹ کارڈ قبول کرتے ہیں؟"
        ),
        PhraseItem(
            id = "s4",
            category = PhraseCategory.SHOPPING,
            english = "I am just looking, thank you",
            turkish = "Sadece bakıyorum, teşekkürler",
            turkishPhonetic = "sah-deh-jeh bah-kuh-yoh-room, teh-shek-koor-lair",
            arabic = "أنا أنظر فقط، شكراً لك",
            urdu = "میں صرف دیکھ رہا ہوں، شکریہ"
        ),
        PhraseItem(
            id = "s5",
            category = PhraseCategory.SHOPPING,
            english = "I will take this one",
            turkish = "Bunu alıyorum",
            turkishPhonetic = "boo-noo ah-luh-yoh-room",
            arabic = "سآخذ هذا",
            urdu = "میں یہ لوں گا"
        ),

        // Hotel & Lodging
        PhraseItem(
            id = "h1",
            category = PhraseCategory.HOTEL,
            english = "I have a reservation",
            turkish = "Rezervasyonum var",
            turkishPhonetic = "reh-zer-vas-yoh-noom var",
            arabic = "لدي حجز مسبق",
            urdu = "میری بکنگ موجود ہے"
        ),
        PhraseItem(
            id = "h2",
            category = PhraseCategory.HOTEL,
            english = "What is the Wi-Fi password?",
            turkish = "Wi-Fi şifresi nedir?",
            turkishPhonetic = "way-fay sheef-reh-see neh-deer?",
            arabic = "ما هي كلمة سر الواي فاي؟",
            urdu = "وائی فائی کا پاس ورڈ کیا ہے؟"
        ),
        PhraseItem(
            id = "h3",
            category = PhraseCategory.HOTEL,
            english = "Can you store my luggage?",
            turkish = "Bavullarımı emanete bırakabilir miyim?",
            turkishPhonetic = "bah-vool-lah-ruh-muh eh-mah-neh-teh buh-rah-kah-bee-leer mee-yeem?",
            arabic = "هل يمكنكم حفظ أمتعتي؟",
            urdu = "کیا میں اپنا سامان رکھوا سکتا ہوں؟"
        ),
        PhraseItem(
            id = "h4",
            category = PhraseCategory.HOTEL,
            english = "What time is check-out?",
            turkish = "Çıkış saati kaçta?",
            turkishPhonetic = "chuh-kuhsh sah-ah-tee kach-tah?",
            arabic = "في أي ساعة وقت المغادرة؟",
            urdu = "چیک آؤٹ کس وقت ہے؟"
        ),

        // Emergency & Health
        PhraseItem(
            id = "e1",
            category = PhraseCategory.EMERGENCY,
            english = "Help me please!",
            turkish = "İmdat! Yardım edin lütfen!",
            turkishPhonetic = "eem-daht! yar-duhm eh-deen loot-fen!",
            arabic = "النجدة! ساعدوني من فضلكم!",
            urdu = "مدد! میری مدد کریں براہ کرم!"
        ),
        PhraseItem(
            id = "e2",
            category = PhraseCategory.EMERGENCY,
            english = "I need a doctor / hospital",
            turkish = "Doktora / Hastaneye ihtiyacım var",
            turkishPhonetic = "dohk-toh-rah / hahs-tah-neh-yeh eeh-tee-yah-juhm var",
            arabic = "أحتاج إلى طبيب / مستشفى",
            urdu = "مجھے ڈاکٹر / ہسپتال کی ضرورت ہے"
        ),
        PhraseItem(
            id = "e3",
            category = PhraseCategory.EMERGENCY,
            english = "Where is the nearest pharmacy?",
            turkish = "En yakın eczane nerede?",
            turkishPhonetic = "en yah-kuhn edj-zah-neh neh-reh-deh?",
            arabic = "أين أقرب صيدلية؟",
            urdu = "قریبی میڈیکل اسٹور (صیدلیہ) کہاں ہے؟"
        ),
        PhraseItem(
            id = "e4",
            category = PhraseCategory.EMERGENCY,
            english = "Call an ambulance (112)",
            turkish = "Ambulans çağırın lütfen",
            turkishPhonetic = "ahm-boo-lahns chah-uh-ruhn loot-fen",
            arabic = "اتصلوا بالإسعاف من فضلكم",
            urdu = "براہ کرم ایمبولینس بلائیں"
        ),
        PhraseItem(
            id = "e5",
            category = PhraseCategory.EMERGENCY,
            english = "I lost my passport",
            turkish = "Pasaportumu kaybettim",
            turkishPhonetic = "pah-sah-por-too-moo kay-bet-teem",
            arabic = "لقد أضعت جواز سفري",
            urdu = "میرا پاسپورٹ گم ہو گیا ہے"
        ),
        PhraseItem(
            id = "e6",
            category = PhraseCategory.EMERGENCY,
            english = "I am allergic to penicillin / nuts",
            turkish = "Penisiline / Kuruyemişe alerjim var",
            turkishPhonetic = "peh-nee-see-lee-neh / koo-roo-yeh-mee-sheh ah-lair-zheem var",
            arabic = "لدي حساسية من البنسلين / المكسرات",
            urdu = "مجھے پینسلین / خشک میوہ جات سے الرجی ہے"
        )
    )
}
