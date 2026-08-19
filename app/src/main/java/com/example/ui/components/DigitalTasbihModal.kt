package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.audio.HapticFeedbackHelper
import com.example.data.PreferencesManager
import com.example.model.AppLanguage
import com.example.ui.i18n.AppStr
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.GeoBorder
import com.example.ui.theme.GeoBorderSubtle
import com.example.ui.theme.GeoContainer
import com.example.ui.theme.GeoOnContainerHigh
import com.example.ui.theme.GeoPrimary
import com.example.ui.theme.GeoSurface
import com.example.ui.theme.GeoSurfaceVariant
import com.example.ui.theme.GeoTextPrimary
import com.example.ui.theme.GeoTextSecondary

data class DhikrItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val defaultTarget: Int = 33,
    val rewardAr: String = "",
    val rewardEn: String = ""
)

enum class AthkarCategory(val titleEn: String, val titleAr: String, val icon: ImageVector) {
    MORNING("Morning Athkar", "أذكار الصباح", Icons.Default.WbSunny),
    EVENING("Evening Athkar", "أذكار المساء", Icons.Default.Bedtime),
    GENERAL("General Tasbih", "التسبيح اليومي", Icons.Default.FilterVintage)
}

val MORNING_ATHKAR_LIST = listOf(
    DhikrItem(
        id = "m_1",
        titleAr = "آية الكرسي",
        titleEn = "Ayat Al-Kursi",
        arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        transliteration = "Ayat Al-Kursi",
        translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.",
        defaultTarget = 1,
        rewardAr = "حفظ ووقاية من الشيطان حتى يمسي",
        rewardEn = "Protected from Satan until evening"
    ),
    DhikrItem(
        id = "m_2",
        titleAr = "سورة الإخلاص",
        titleEn = "Surat Al-Ikhlas",
        arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        transliteration = "Surat Al-Ikhlas (3x)",
        translation = "Say, He is Allah, [who is] One. Allah, the Eternal Refuge. He neither begets nor is born, Nor is there to Him any equivalent.",
        defaultTarget = 3,
        rewardAr = "تكفيك من كل شيء",
        rewardEn = "Suffices you against everything"
    ),
    DhikrItem(
        id = "m_3",
        titleAr = "سورة الفلق",
        titleEn = "Surat Al-Falaq",
        arabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        transliteration = "Surat Al-Falaq (3x)",
        translation = "Say, I seek refuge in the Lord of daybreak From the evil of that which He created, And from the evil of darkness when it settles, And from the evil of the blowers in knots, And from the evil of an envier when he envies.",
        defaultTarget = 3,
        rewardAr = "حفظ ووقاية من كل مكروه",
        rewardEn = "Protection from harm"
    ),
    DhikrItem(
        id = "m_4",
        titleAr = "سورة الناس",
        titleEn = "Surat An-Nas",
        arabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ",
        transliteration = "Surat An-Nas (3x)",
        translation = "Say, I seek refuge in the Lord of mankind, The Sovereign of mankind, The God of mankind, From the evil of the retreating whisperer - Who whispers into the breasts of mankind - From among the jinn and mankind.",
        defaultTarget = 3,
        rewardAr = "حفظ ووقاية من الوساوس والشرور",
        rewardEn = "Protection from whispers & evil"
    ),
    DhikrItem(
        id = "m_5",
        titleAr = "أصبحنا وأصبح الملك لله",
        titleEn = "Asbahna wa Asbaha",
        arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
        transliteration = "Asbahna wa Asbaha al-Mulku Lillah",
        translation = "We have entered the morning and all dominion belongs to Allah, and all praise is to Allah. There is no god but Allah alone, without partner. His is the sovereignty and His is the praise, and He is over all things competent. My Lord, I ask You for the good of this day and what comes after it.",
        defaultTarget = 1
    ),
    DhikrItem(
        id = "m_6",
        titleAr = "سيد الاستغفار",
        titleEn = "Sayyid Al-Istighfar",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
        transliteration = "Sayyid Al-Istighfar",
        translation = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can. I seek refuge in You from the evil of what I have done. I acknowledge Your favor upon me and I acknowledge my sin, so forgive me, for none forgives sins except You.",
        defaultTarget = 1,
        rewardAr = "من قالها موقناً بها فمات من يومه دخل الجنة",
        rewardEn = "Whoever says it with conviction and dies that day enters Paradise"
    ),
    DhikrItem(
        id = "m_7",
        titleAr = "اللهم بك أصبحنا",
        titleEn = "Allahumma bika Asbahna",
        arabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
        transliteration = "Allahumma bika asbahna wa bika amsayna",
        translation = "O Allah, by You we enter the morning and by You we enter the evening, by You we live and by You we die, and to You is the resurrection.",
        defaultTarget = 1
    ),
    DhikrItem(
        id = "m_8",
        titleAr = "بسم الله الذي لا يضر",
        titleEn = "Bismillahil-ladhi",
        arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        transliteration = "Bismillahil-ladhi la yadurru ma'asmihi shay'",
        translation = "In the Name of Allah with Whose Name nothing can cause harm on earth or in the heavens, and He is the All-Hearing, the All-Knowing.",
        defaultTarget = 3,
        rewardAr = "لم يضره شيء في ذلك اليوم",
        rewardEn = "Nothing will harm whoever recites it 3 times"
    ),
    DhikrItem(
        id = "m_9",
        titleAr = "رضيت بالله رباً",
        titleEn = "Raditu Billahi",
        arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        transliteration = "Raditu Billahi Rabba",
        translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (pbuh) as my Prophet.",
        defaultTarget = 3,
        rewardAr = "كان حقاً على الله أن يرضيه يوم القيامة",
        rewardEn = "Allah has promised to please the reciter on the Day of Judgment"
    ),
    DhikrItem(
        id = "m_10",
        titleAr = "يا حي يا قيوم",
        titleEn = "Ya Hayyu Ya Qayyum",
        arabic = "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ، وَلَا تَكِلْنِي إِلَى نَفْسِي طَرْفَةَ عَيْنٍ",
        transliteration = "Ya Hayyu Ya Qayyum bi rahmatika astagheeth",
        translation = "O Ever-Living, O Self-Sustaining, by Your mercy I seek assistance; rectify for me all of my affairs and do not leave me to myself even for the blink of an eye.",
        defaultTarget = 1
    ),
    DhikrItem(
        id = "m_11",
        titleAr = "سبحان الله وبحمده (100)",
        titleEn = "Subhan Allahi (100x)",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        transliteration = "Subhan Allahi wa Bihamdihi",
        translation = "Glory is to Allah and praise is to Him.",
        defaultTarget = 100,
        rewardAr = "حُطّت خطاياه وإن كانت مثل زبد البحر",
        rewardEn = "Sins are forgiven even if they were like the foam of the sea"
    )
)

val EVENING_ATHKAR_LIST = listOf(
    DhikrItem(
        id = "e_1",
        titleAr = "آية الكرسي",
        titleEn = "Ayat Al-Kursi",
        arabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَنْ ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ",
        transliteration = "Ayat Al-Kursi",
        translation = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth.",
        defaultTarget = 1,
        rewardAr = "حفظ ووقاية من الشيطان حتى يصبح",
        rewardEn = "Protected from Satan until morning"
    ),
    DhikrItem(
        id = "e_2",
        titleAr = "سورة الإخلاص",
        titleEn = "Surat Al-Ikhlas",
        arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ ۝ اللَّهُ الصَّمَدُ ۝ لَمْ يَلِدْ وَلَمْ يُولَدْ ۝ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
        transliteration = "Surat Al-Ikhlas (3x)",
        translation = "Say, He is Allah, [who is] One. Allah, the Eternal Refuge. He neither begets nor is born, Nor is there to Him any equivalent.",
        defaultTarget = 3,
        rewardAr = "تكفيك من كل شيء",
        rewardEn = "Suffices you against everything"
    ),
    DhikrItem(
        id = "e_3",
        titleAr = "سورة الفلق",
        titleEn = "Surat Al-Falaq",
        arabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ ۝ مِن شَرِّ مَا خَلَقَ ۝ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ ۝ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ ۝ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
        transliteration = "Surat Al-Falaq (3x)",
        translation = "Say, I seek refuge in the Lord of daybreak From the evil of that which He created, And from the evil of darkness when it settles.",
        defaultTarget = 3,
        rewardAr = "حفظ ووقاية من كل مكروه",
        rewardEn = "Protection from harm"
    ),
    DhikrItem(
        id = "e_4",
        titleAr = "سورة الناس",
        titleEn = "Surat An-Nas",
        arabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ ۝ مَلِكِ النَّاسِ ۝ إِلَٰهِ النَّاسِ ۝ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ ۝ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ ۝ مِنَ الْجِنَّةِ وَالنَّاسِ",
        transliteration = "Surat An-Nas (3x)",
        translation = "Say, I seek refuge in the Lord of mankind, The Sovereign of mankind, The God of mankind.",
        defaultTarget = 3,
        rewardAr = "حفظ ووقاية من الوساوس والشرور",
        rewardEn = "Protection from whispers & evil"
    ),
    DhikrItem(
        id = "e_5",
        titleAr = "أمسينا وأمسى الملك لله",
        titleEn = "Amsayna wa Amsa",
        arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذِهِ اللَّيْلَةِ وَخَيْرَ مَا بَعْدَهَا، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذِهِ اللَّيْلَةِ وَشَرِّ مَا بَعْدَهَا، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
        transliteration = "Amsayna wa Amsa al-Mulku Lillah",
        translation = "We have reached the evening and all dominion belongs to Allah, and all praise is to Allah. There is no god but Allah alone, without partner.",
        defaultTarget = 1
    ),
    DhikrItem(
        id = "e_6",
        titleAr = "سيد الاستغفار",
        titleEn = "Sayyid Al-Istighfar",
        arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عهدك ووعدك ما استطعت، أعوذ بك من شر ما صنعت، أبوء لك بنعمتك عليّ، وأبوء بذنبي فاغفر لي فإنه لا يغفر الذنوب إلا أنت",
        transliteration = "Sayyid Al-Istighfar",
        translation = "O Allah, You are my Lord; there is no deity except You. You created me and I am Your servant, and I abide by Your covenant and promise as best I can.",
        defaultTarget = 1,
        rewardAr = "من قالها موقناً بها فمات من ليلته دخل الجنة",
        rewardEn = "Whoever says it with conviction and dies that night enters Paradise"
    ),
    DhikrItem(
        id = "e_7",
        titleAr = "أعوذ بكلمات الله التامات",
        titleEn = "A'udhu bi Kalimatillah",
        arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
        transliteration = "A'udhu bi kalimatil-lahit-tammati min sharri ma khalaq",
        translation = "I seek refuge in the Perfect Words of Allah from the evil of what He has created.",
        defaultTarget = 3,
        rewardAr = "لم يضره شيء تلك الليلة",
        rewardEn = "No harm will touch the reciter that night"
    ),
    DhikrItem(
        id = "e_8",
        titleAr = "اللهم بك أمسينا",
        titleEn = "Allahumma bika Amsayna",
        arabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
        transliteration = "Allahumma bika amsayna wa bika asbahna",
        translation = "O Allah, by You we enter the evening and by You we enter the morning, by You we live and by You we die, and to You is the final return.",
        defaultTarget = 1
    ),
    DhikrItem(
        id = "e_9",
        titleAr = "بسم الله الذي لا يضر",
        titleEn = "Bismillahil-ladhi",
        arabic = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
        transliteration = "Bismillahil-ladhi la yadurru ma'asmihi shay'",
        translation = "In the Name of Allah with Whose Name nothing can cause harm on earth or in the heavens, and He is the All-Hearing, the All-Knowing.",
        defaultTarget = 3
    ),
    DhikrItem(
        id = "e_10",
        titleAr = "رضيت بالله رباً",
        titleEn = "Raditu Billahi",
        arabic = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا",
        transliteration = "Raditu Billahi Rabba",
        translation = "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad (pbuh) as my Prophet.",
        defaultTarget = 3
    ),
    DhikrItem(
        id = "e_11",
        titleAr = "سبحان الله وبحمده (100)",
        titleEn = "Subhan Allahi (100x)",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        transliteration = "Subhan Allahi wa Bihamdihi",
        translation = "Glory is to Allah and praise is to Him.",
        defaultTarget = 100
    )
)

val GENERAL_DHIKR_LIST = listOf(
    DhikrItem(
        id = "g_1",
        titleAr = "سبحان الله",
        titleEn = "SubhanAllah",
        arabic = "سُبْحَانَ اللَّهِ",
        transliteration = "SubhanAllah",
        translation = "Glory be to Allah",
        defaultTarget = 33
    ),
    DhikrItem(
        id = "g_2",
        titleAr = "الحمد لله",
        titleEn = "Alhamdulillah",
        arabic = "الْحَمْدُ لِلَّهِ",
        transliteration = "Alhamdulillah",
        translation = "Praise be to Allah",
        defaultTarget = 33
    ),
    DhikrItem(
        id = "g_3",
        titleAr = "الله أكبر",
        titleEn = "Allahu Akbar",
        arabic = "اللَّهُ أَكْبَرُ",
        transliteration = "Allahu Akbar",
        translation = "Allah is the Greatest",
        defaultTarget = 33
    ),
    DhikrItem(
        id = "g_4",
        titleAr = "لا إله إلا الله",
        titleEn = "La ilaha illa Allah",
        arabic = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
        transliteration = "La ilaha illa Allah",
        translation = "There is no deity except Allah alone, without partner",
        defaultTarget = 100
    ),
    DhikrItem(
        id = "g_5",
        titleAr = "أستغفر الله",
        titleEn = "Astaghfirullah",
        arabic = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
        transliteration = "Astaghfirullah wa Atubu Ilayh",
        translation = "I seek forgiveness of Allah and repent to Him",
        defaultTarget = 100
    ),
    DhikrItem(
        id = "g_6",
        titleAr = "لا حول ولا قوة إلا بالله",
        titleEn = "La Hawla wa la Quwwata",
        arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ الْعَلِيِّ الْعَظِيمِ",
        transliteration = "La hawla wa la quwwata illa billah",
        translation = "There is no power nor strength except with Allah",
        defaultTarget = 33,
        rewardAr = "كنز من كنوز الجنة",
        rewardEn = "A treasure from the treasures of Paradise"
    ),
    DhikrItem(
        id = "g_7",
        titleAr = "الصلاة على النبي",
        titleEn = "Salawat",
        arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ وَبَارِكْ عَلَى نَبِيِّنَا مُحَمَّدٍ وَعَلَى آلِهِ وَصَحْبِهِ أَجْمَعِينَ",
        transliteration = "Allahumma Salli 'ala Muhammad",
        translation = "O Allah, send blessings and peace upon our Prophet Muhammad",
        defaultTarget = 100
    ),
    DhikrItem(
        id = "g_8",
        titleAr = "سبحان الله العظيم",
        titleEn = "SubhanAllahil-Adheem",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
        transliteration = "SubhanAllahi wa bihamdihi, SubhanAllahil-Adheem",
        translation = "Glory to Allah and praise Him, Glory to Allah the Supreme",
        defaultTarget = 100,
        rewardAr = "حبيبتان إلى الرحمن، ثقيلتان في الميزان",
        rewardEn = "Beloved to the Most Merciful, heavy on the scales"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalTasbihModal(
    language: AppLanguage = AppLanguage.ENGLISH,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val hapticHelper = remember { HapticFeedbackHelper(context) }
    val prefsManager = remember { PreferencesManager(context) }
    val isArabic = language == AppLanguage.ARABIC

    // Load persisted state
    val savedState = remember { prefsManager.loadTasbihState() }

    var selectedCategory by remember { mutableStateOf(AthkarCategory.MORNING) }
    val currentDhikrList = when (selectedCategory) {
        AthkarCategory.MORNING -> MORNING_ATHKAR_LIST
        AthkarCategory.EVENING -> EVENING_ATHKAR_LIST
        AthkarCategory.GENERAL -> GENERAL_DHIKR_LIST
    }

    var selectedDhikrIndex by remember { mutableIntStateOf(0) }
    val activeDhikr = currentDhikrList.getOrElse(selectedDhikrIndex) { currentDhikrList[0] }

    var count by remember { mutableIntStateOf(savedState.currentCount) }
    var rounds by remember { mutableIntStateOf(savedState.rounds) }
    var totalCount by remember { mutableIntStateOf(savedState.totalCount) }
    var targetCount by remember { mutableIntStateOf(activeDhikr.defaultTarget) }

    var hapticsOn by remember { mutableStateOf(true) }
    var soundOn by remember { mutableStateOf(false) }
    var isTapped by remember { mutableStateOf(false) }
    var showMasterResetDialog by remember { mutableStateOf(false) }

    // When category changes, reset selected index and target
    LaunchedEffect(selectedCategory) {
        selectedDhikrIndex = 0
        targetCount = currentDhikrList[0].defaultTarget
    }

    // Update target when dhikr item changes
    LaunchedEffect(selectedDhikrIndex, selectedCategory) {
        targetCount = activeDhikr.defaultTarget
    }

    // Auto-save whenever count, rounds, or total change
    LaunchedEffect(count, rounds, totalCount) {
        prefsManager.saveTasbihState(count, rounds, totalCount)
    }

    val progress = if (targetCount > 0) (count.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "tasbih_progress"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (isTapped) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        finishedListener = { isTapped = false },
        label = "tap_scale"
    )

    fun handleIncrement() {
        isTapped = true
        val newCount = count + 1
        totalCount += 1

        if (targetCount > 0 && newCount >= targetCount) {
            count = 0
            rounds += 1
            if (hapticsOn) hapticHelper.triggerAlignmentHaptic()
            if (soundOn) hapticHelper.triggerAlignmentTone()
        } else {
            count = newCount
            if (hapticsOn) hapticHelper.triggerTick()
        }
    }

    fun handleSessionReset() {
        count = 0
        rounds = 0
        if (hapticsOn) hapticHelper.triggerTick()
    }

    fun handleMasterReset() {
        count = 0
        rounds = 0
        totalCount = 0
        prefsManager.resetTasbihAll()
        if (hapticsOn) hapticHelper.triggerTick()
    }

    // Full-screen Dialog
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("full_screen_tasbih"),
            color = GeoBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("tasbih_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GeoTextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isArabic) "الأذكار والتسبيح" else "Athkar & Tasbih",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary
                        )
                        Text(
                            text = if (isArabic) "المجموع الكلي: $totalCount" else "Lifetime: $totalCount",
                            fontSize = 12.sp,
                            color = GeoPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Reset Current Session
                        IconButton(
                            onClick = { handleSessionReset() },
                            modifier = Modifier.size(36.dp).testTag("tasbih_session_reset_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Session",
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Master Reset All
                        IconButton(
                            onClick = { showMasterResetDialog = true },
                            modifier = Modifier.size(36.dp).testTag("tasbih_master_reset_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Master Reset",
                                tint = GeoTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Category Tabs (Morning / Evening / General)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GeoSurfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AthkarCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GeoPrimary else Color.Transparent)
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else GeoTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isArabic) cat.titleAr else cat.titleEn.split(" ")[0],
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else GeoTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Horizontal Dhikr Carousel / Selector
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(currentDhikrList) { index, item ->
                        val isSelected = index == selectedDhikrIndex
                        val displayTitle = if (isArabic) item.titleAr else item.titleEn
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) GeoContainer else GeoSurfaceVariant)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) GeoPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    selectedDhikrIndex = index
                                    count = 0
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GeoPrimary else GeoTextSecondary
                                )
                                Text(
                                    text = displayTitle,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GeoTextPrimary else GeoTextSecondary,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Main Dhikr Display Card (Scrollable for full-length Azhkar while preserving layout)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .heightIn(min = 90.dp, max = 150.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GeoSurfaceVariant)
                        .border(1.dp, GeoBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Full Arabic Text
                        Text(
                            text = activeDhikr.arabic,
                            fontSize = if (activeDhikr.arabic.length > 150) 15.sp else 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary,
                            textAlign = TextAlign.Center,
                            lineHeight = if (activeDhikr.arabic.length > 150) 24.sp else 26.sp
                        )

                        // English Translation (Only shown when app language is English)
                        if (!isArabic && activeDhikr.translation.isNotEmpty()) {
                            Text(
                                text = activeDhikr.translation,
                                fontSize = 11.sp,
                                color = GeoTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }

                        // Reward / Fadl badge
                        val rewardText = if (isArabic) activeDhikr.rewardAr else if (activeDhikr.rewardEn.isNotEmpty()) activeDhikr.rewardEn else activeDhikr.rewardAr
                        if (rewardText.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GeoContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "✨ $rewardText",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GeoPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Circular Progress Tap Counter
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(scaleAnim)
                        .clip(CircleShape)
                        .background(GeoSurfaceVariant)
                        .border(6.dp, GeoSurface, CircleShape)
                        .clickable { handleIncrement() }
                        .testTag("tasbih_main_tap_circle"),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = GeoPrimary,
                        strokeWidth = 10.dp,
                        trackColor = GeoBorderSubtle,
                        strokeCap = StrokeCap.Round
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = GeoTextPrimary,
                            letterSpacing = (-1).sp
                        )

                        Text(
                            text = if (targetCount > 0) "/ $targetCount" else "∞",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GeoTextSecondary
                        )

                        if (rounds > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(GeoContainer)
                                    .padding(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "الدورة: $rounds" else "Round: $rounds",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GeoPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Controls: Target Switcher & Sound/Haptic Toggles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Target selector pills
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(33, 99, 100, 0).forEach { tgt ->
                            val isSelected = targetCount == tgt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GeoPrimary else GeoSurfaceVariant)
                                    .clickable {
                                        targetCount = tgt
                                        count = 0
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (tgt == 0) "∞" else "$tgt",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else GeoTextSecondary
                                )
                            }
                        }
                    }

                    // Toggles for Haptic & Sound
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { hapticsOn = !hapticsOn },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (hapticsOn) GeoContainer else GeoSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Haptics",
                                tint = if (hapticsOn) GeoPrimary else GeoTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { soundOn = !soundOn },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (soundOn) GeoContainer else GeoSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Sound",
                                tint = if (soundOn) GeoPrimary else GeoTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Master Reset
    if (showMasterResetDialog) {
        BasicAlertDialog(
            onDismissRequest = { showMasterResetDialog = false }
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = GeoSurface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GeoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = GeoPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Text(
                        text = if (isArabic) "إعادة تعيين شاملة للعداد؟" else "Master Reset Counter?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GeoTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isArabic)
                            "سيتم تصفير العداد الحالي والدورات والمجموع التراكمي بالكامل."
                        else
                            "This will reset your current count, rounds, and total lifetime count to zero.",
                        fontSize = 13.sp,
                        color = GeoTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = { showMasterResetDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isArabic) "إلغاء" else "Cancel",
                                color = GeoTextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                handleMasterReset()
                                showMasterResetDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GeoPrimary)
                        ) {
                            Text(
                                text = if (isArabic) "تصفير الكل" else "Reset All",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
