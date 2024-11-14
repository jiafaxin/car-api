package com.autohome.car.api.compare;

import com.autohome.car.api.compare.tools.CompareJson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class ConfigGetListBySpecId {
    public static void main(String[] args) {
        SpringApplication.run(ConfigGetListBySpecId.class, args);

        String od = "http://car.api.autohome.com.cn";
        String nd = "http://car-car-api-compare-test-http.thallo.corpautohome.com";

        List<CompletableFuture> tasks = new ArrayList<>();

        for (int i = 0; i <= 63265; i++) {
            if(escapeSpec.contains(i)){
                continue;
            }
            String url = "/v3/CarPrice/Config_GetListBySpecId.ashx?_appid=app&specid=" + i;

 //           tasks.add(CompletableFuture.runAsync(()->{
                new CompareJson().exclude().compareUrl(od.concat(url), nd.concat(url));
//            }));
//            if(tasks.size()%30==0){
//                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
//                tasks.clear();
//            }

            if(i%1000 == 0){
                System.out.println(i);
            }
        }

        for (int i = 1000000; i <= 1016180; i++) {
            if(escapeSpec.contains(i)){
                continue;
            }
            String url = "/v3/CarPrice/Config_GetListBySpecId.ashx?_appid=app&specid=" + i;
//            tasks.add(CompletableFuture.runAsync(()->{
                new CompareJson().exclude().compareUrl(od.concat(url), nd.concat(url));
//            }));
//            if(tasks.size()%30==0){
//                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
//                tasks.clear();
//            }
            if(i%1000 == 0){
                System.out.println(i);
            }
        }

        System.out.println("=== success =================================");
    }

    /**
     * SELECT distinct concat( SpecId,',')  FROM ConfigSpecRelation as A WITH(NOLOCK) where  NOT EXISTS (
     *   SELECT 1
     *   FROM spec_new sn WITH(NOLOCK)
     *   WHERE sn.id = A.SpecId
     * )
     * union ALL
     * SELECT distinct concat( SpecId,',')  FROM CV_ConfigSpecRelation as A WITH(NOLOCK) where  NOT EXISTS (
     *   SELECT 1
     *   FROM cv_spec sn WITH(NOLOCK)
     *   WHERE sn.id = A.SpecId
     * )
     */
    static Set<Integer> escapeSpec = new HashSet<>(Arrays.asList(12255,
            12712,
            12919,
            13505,
            13520,
            14570,
            15433,
            16934,
            17469,
            17714,
            17719,
            17720,
            17721,
            17722,
            17724,
            17791,
            17940,
            18093,
            18125,
            18193,
            18321,
            18452,
            18469,
            18744,
            18932,
            19012,
            19164,
            19184,
            19639,
            19827,
            19854,
            20297,
            20486,
            20487,
            21024,
            21025,
            21160,
            21219,
            21389,
            21462,
            21472,
            21913,
            22224,
            22231,
            22438,
            22453,
            22480,
            22502,
            22526,
            22575,
            22580,
            22596,
            23266,
            23452,
            23537,
            23539,
            23541,
            23687,
            23688,
            23689,
            23846,
            24078,
            24174,
            24268,
            24269,
            24270,
            24271,
            24536,
            24540,
            24715,
            25125,
            25126,
            25127,
            25440,
            25447,
            25448,
            25665,
            25666,
            25667,
            25668,
            25669,
            25671,
            25674,
            25675,
            25677,
            25678,
            25679,
            25680,
            25923,
            25924,
            25925,
            26045,
            26046,
            26047,
            26049,
            26050,
            26051,
            26076,
            26115,
            26116,
            26129,
            27034,
            27035,
            27416,
            27417,
            27418,
            27419,
            27426,
            27427,
            27434,
            27435,
            27465,
            27467,
            27515,
            27541,
            27748,
            28154,
            28166,
            29139,
            29527,
            29528,
            30027,
            30028,
            30029,
            30030,
            30515,
            30612,
            30676,
            30677,
            30940,
            30969,
            30970,
            30971,
            30972,
            31221,
            31222,
            31409,
            31700,
            32011,
            32616,
            32672,
            32997,
            33000,
            33003,
            33004,
            33005,
            33006,
            33007,
            33087,
            33088,
            33089,
            33844,
            33845,
            33846,
            33847,
            33848,
            33849,
            33875,
            34748,
            35155,
            35198,
            35482,
            36220,
            36221,
            36222,
            36225,
            36226,
            36687,
            36688,
            36689,
            36690,
            36691,
            36775,
            36777,
            36804,
            37151,
            37154,
            37264,
            37265,
            37266,
            37396,
            37557,
            37559,
            37857,
            38299,
            39140,
            39142,
            39143,
            39220,
            39221,
            39222,
            39223,
            39224,
            39225,
            39226,
            39227,
            39521,
            39539,
            39611,
            39650,
            39651,
            39653,
            39655,
            39705,
            39755,
            39756,
            39759,
            39760,
            40235,
            40236,
            40237,
            40238,
            40239,
            40240,
            40241,
            40242,
            40243,
            40244,
            40422,
            40423,
            40424,
            40426,
            40427,
            40428,
            40430,
            40431,
            40432,
            41675,
            41676,
            41677,
            41678,
            41679,
            41680,
            41681,
            41682,
            41839,
            41840,
            41841,
            41842,
            41843,
            41844,
            41851,
            41853,
            41854,
            41855,
            41856,
            41857,
            41858,
            41859,
            41865,
            41866,
            41867,
            41868,
            41869,
            41870,
            41872,
            41873,
            41874,
            41875,
            41877,
            41878,
            41879,
            41885,
            41887,
            41889,
            41891,
            41893,
            41895,
            41897,
            41899,
            41901,
            41902,
            41903,
            42006,
            42007,
            42008,
            42009,
            42010,
            42011,
            42012,
            42095,
            42096,
            42097,
            42098,
            42099,
            42100,
            42101,
            42182,
            42183,
            42184,
            42185,
            42186,
            42187,
            42258,
            42259,
            42260,
            42261,
            42262,
            42263,
            42606,
            42609,
            42610,
            42830,
            42831,
            42832,
            42833,
            42834,
            42835,
            43113,
            43114,
            43449,
            43450,
            43451,
            43452,
            43453,
            43454,
            43580,
            43602,
            43609,
            43656,
            43657,
            43658,
            43659,
            43660,
            43661,
            43662,
            43663,
            43664,
            43665,
            43666,
            43667,
            43668,
            43669,
            43670,
            43671,
            43672,
            43704,
            43705,
            43706,
            43707,
            43708,
            43709,
            43710,
            43711,
            43717,
            43718,
            43719,
            43720,
            43721,
            43722,
            43723,
            43724,
            43806,
            43807,
            43808,
            43809,
            43810,
            43811,
            43812,
            43813,
            43814,
            43815,
            43816,
            43817,
            43818,
            43819,
            43826,
            43827,
            43828,
            43829,
            43833,
            43834,
            43835,
            43878,
            43879,
            43880,
            43881,
            43882,
            43883,
            43884,
            43902,
            43903,
            43904,
            43905,
            43906,
            43907,
            43909,
            43910,
            43911,
            43912,
            43913,
            43962,
            43963,
            43964,
            43965,
            43966,
            43967,
            43989,
            43990,
            43991,
            43992,
            43993,
            43994,
            43995,
            43996,
            44008,
            44009,
            44010,
            44012,
            44013,
            44014,
            44068,
            44069,
            4407,
            44071,
            44072,
            44073,
            44074,
            44075,
            44076,
            44124,
            44125,
            44126,
            44127,
            44128,
            44129,
            44169,
            44170,
            44171,
            44191,
            44192,
            44193,
            44194,
            44195,
            44196,
            44259,
            44260,
            44261,
            44262,
            44263,
            44692,
            44697,
            44698,
            44705,
            44706,
            44774,
            44777,
            44778,
            44783,
            45087,
            45176,
            45177,
            45589,
            45590,
            45591,
            45593,
            45984,
            45985,
            45986,
            47382,
            47383,
            47384,
            47385,
            47475,
            48229,
            48282,
            48483,
            48591,
            48592,
            48807,
            48824,
            48825,
            48826,
            48827,
            48828,
            48829,
            48830,
            49206,
            49387,
            50021,
            50108,
            50259,
            50412,
            50413,
            50414,
            50415,
            50416,
            50417,
            50418,
            50419,
            50420,
            50421,
            51367,
            51407,
            51409,
            51762,
            51928,
            51930,
            51931,
            52405,
            52406,
            52407,
            52826,
            53012,
            54101,
            54102,
            54103,
            54105,
            54106,
            55675,
            55676,
            55677,
            55678,
            55952,
            55954,
            56709,
            56711,
            57186,
            57187,
            57188,
            57189,
            57190,
            57635,
            57636,
            6310,
            6311,
            6312,
            6313,
            6314,
            6659,
            9760,
            1008314,
            1013161,
            1004866,
            1003820,
            1009006,
            1010207,
            1010224,
            1004166,
            1010214,
            1005623,
            1004354,
            1003376,
            1007290,
            1010847,
            1010844,
            1005478,
            1013158,
            1010233,
            1010242,
            1010222,
            1003630,
            1002172,
            1003818,
            1004939,
            1010209,
            1004167,
            1007101,
            1010253,
            1010206,
            1003825,
            1010859,
            1010244,
            1004081,
            1007890,
            1004406,
            1010857,
            1010231,
            1013171,
            1005501,
            1013156,
            1010862,
            1007156,
            1010226,
            1010860,
            1014095,
            1004845,
            1010255,
            1012109,
            1010212,
            1006531,
            1006629,
            1005621,
            1005482,
            1011724,
            1004940,
            1007289,
            1004404,
            1006670,
            1004044,
            1003470,
            1010220,
            1005626,
            1010210,
            1005211,
            1006633,
            1010228,
            1006645,
            1010218,
            1005481,
            1013167,
            1003824,
            1010794,
            1013517,
            1004803,
            1004848,
            1006534,
            1010202,
            1010204,
            1004844,
            1007105,
            1003819,
            1006639,
            1003828,
            1010237,
            1013152,
            1010858,
            1004172,
            1006968,
            1005104,
            1004200,
            1010239,
            1005500,
            1003826,
            1013153,
            1010235,
            1009090,
            1004808,
            1004846,
            1014096,
            1003823,
            1004173,
            1004242,
            1013155,
            1005627,
            1007613,
            1004082,
            1012471,
            1005503,
            1010790,
            1010240,
            1010861,
            1010849,
            1010856,
            1010248,
            1010257,
            1005485,
            1007736,
            1004843,
            1010211,
            1010256,
            1009092,
            1003743,
            1009091,
            1012924,
            1013160,
            1010789,
            1004169,
            1005033,
            1010223,
            1010213,
            1009003,
            1013165,
            1006672,
            1010236,
            1009008,
            1007899,
            1007607,
            1010215,
            1005498,
            1010842,
            1005108,
            1013169,
            1004027,
            1004351,
            1010241,
            1010852,
            1010208,
            1010221,
            1006627,
            1002171,
            1010848,
            1010234,
            1006970,
            1007891,
            1003829,
            1010855,
            1011921,
            1006630,
            1005110,
            1010254,
            1005484,
            1010232,
            1010853,
            1001627,
            1003822,
            1010252,
            1010243,
            1007103,
            1013173,
            1010850,
            1010843,
            1010846,
            1007288,
            1005624,
            1004170,
            1010793,
            1004849,
            1004842,
            1006533,
            1005622,
            1013163,
            1004174,
            1006631,
            1010245,
            1006331,
            1006638,
            1006333,
            1013162,
            1004352,
            1001626,
            1010227,
            1004807,
            1010217,
            1009004,
            1010841,
            1001172,
            1005106,
            1010229,
            1004168,
            1004694,
            1004847,
            1007218,
            1010219,
            1010225,
            1012467,
            1010251,
            1007612,
            1010840,
            1010839,
            1004353,
            1010203,
            1003587,
            1010216,
            1010238,
            1009005,
            1009088,
            1010230,
            1010851,
            1004810,
            1010205,
            1009089,
            1005045,
            1001180,
            1005029,
            1006617,
            1004043,
            1012468,
            1004199,
            1004024,
            1010250,
            1010845,
            1003741,
            1013154,
            1010249,
            1010247,
            1004171,
            1005502,
            1003827,
            1004026,
            1005479,
            1003923,
            1004841,
            1010854,
            1005480,
            1005041,
            1010246,
            1003821,
            1009007,
            1005483,
            1005625,
            1005037,
            1001176,
            1006532,
            1006616));
}
