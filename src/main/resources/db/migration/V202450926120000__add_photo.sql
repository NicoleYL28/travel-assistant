UPDATE recommendations
SET image_url = CASE tag_id
                    WHEN 5 THEN 'https://tse3.mm.bing.net/th/id/OIP.cL86s3iB53uL6qKWb6djlwHaE_?rs=1&pid=ImgDetMain&o=7&rm=3'
                    WHEN 6 THEN 'https://th.bing.com/th/id/R.69d0932e3e15c3c4f8b1fc011e3a1b26?rik=svmx8rlL2%2blwBQ&riu=http%3a%2f%2fimg1.qunarzz.com%2fdouxing%2fi7%2f1511%2f24%2f76d8b39f5ad194f7.jpg%3f1600_1066&ehk=57Ww%2bTydcbTGYGi2%2fHlygJoyfC7zd%2fCbHX9NipeMQG8%3d&risl=&pid=ImgRaw&r=0'
                    WHEN 7 THEN 'https://pic1.zhimg.com/v2-1c0a6eadc663832aae33e2c2bf21f7e4_1440w.jpg?source=172ae18b'
                    WHEN 8 THEN 'https://tse3.mm.bing.net/th/id/OIP.cL86s3iB53uL6qKWb6djlwHaE_?rs=1&pid=ImgDetMain&o=7&rm=3'
                    WHEN 9 THEN 'https://th.bing.com/th/id/R.69d0932e3e15c3c4f8b1fc011e3a1b26?rik=svmx8rlL2%2blwBQ&riu=http%3a%2f%2fimg1.qunarzz.com%2fdouxing%2fi7%2f1511%2f24%2f76d8b39f5ad194f7.jpg%3f1600_1066&ehk=57Ww%2bTydcbTGYGi2%2fHlygJoyfC7zd%2fCbHX9NipeMQG8%3d&risl=&pid=ImgRaw&r='
                    WHEN 10 THEN 'https://tse3.mm.bing.net/th/id/OIP.0O5Xe8DwKsPAbjYkmwIV5AHaDc?rs=1&pid=ImgDetMain&o=7&rm=3'
                    WHEN 11 THEN 'https://tse3.mm.bing.net/th/id/OIP.0O5Xe8DwKsPAbjYkmwIV5AHaDc?rs=1&pid=ImgDetMain&o=7&rm=3'
                    WHEN 12 THEN 'https://tse4.mm.bing.net/th/id/OIP.b8uRhDRpX_rzQr8NfRM_0AHaFj?rs=1&pid=ImgDetMain&o=7&r…'
                    WHEN 15 THEN 'https://pic1.zhimg.com/v2-1c0a6eadc663832aae33e2c2bf21f7e4_1440w.jpg?source=172ae18b'
                    WHEN 16 THEN 'https://dimg04.c-ctrip.com/images/0303s12000n94rkhbA2DC_C_420_420.jpg'
                    WHEN 17 THEN 'https://q2.itc.cn/q_70/images01/20250611/4fab94b217864b188a14f408d1a7ef3b.jpeg'
                    WHEN 20 THEN 'https://th.bing.com/th/id/R.69d0932e3e15c3c4f8b1fc011e3a1b26?rik=svmx8rlL2%2blwBQ&riu=http%3a%2f%2fimg1.qunarzz.com%2fdouxing%2fi7%2f1511%2f24%2f76d8b39f5ad194f7.jpg%3f1600_1066&ehk=57Ww%2bTydcbTGYGi2%2fHlygJoyfC7zd%2fCbHX9NipeMQG8%3d&risl=&pid=ImgRaw&r=0'
                    WHEN 21 THEN 'https://pic1.zhimg.com/v2-1c0a6eadc663832aae33e2c2bf21f7e4_1440w.jpg?source=172ae18b'
                    WHEN 22 THEN 'https://dimg04.c-ctrip.com/images/0303s12000n94rkhbA2DC_C_420_420.jpg'
                    WHEN 23 THEN 'https://q2.itc.cn/q_70/images01/20250611/4fab94b217864b188a14f408d1a7ef3b.jpeg'
                    WHEN 24 THEN 'https://tse4.mm.bing.net/th/id/OIP.b8uRhDRpX_rzQr8NfRM_0AHaFj?rs=1&pid=ImgDetMain&o=7&r…'
    END
WHERE tag_id IN (5,6,7,8,9,10,11,12,15,16,17.20,21,22,23,24);

UPDATE recommendations
SET image_url = CASE recommendation_id
                    WHEN 18 THEN 'https://tse2.mm.bing.net/th/id/OIP.53dyYazx9UgNVIm3AO_9FwHaEK?rs=1&pid=ImgDetMain&o=7&r…'
                    WHEN 19 THEN 'https://img95.699pic.com/photo/50064/1380.jpg_wh860.jpg'
                    WHEN 20 THEN 'https://img1.qunarzz.com/travel/poi/201404/11/384665f4c9f16feeddb12cfb.jpg_r_1024x683x95_10086f8b.jpg'
                    WHEN 21 THEN 'https://x0.ifengimg.com/ucms/2021_48/5F2C9EECB0DD86E00ACEC889BAB66ADB00B1BE8C_size199_w1080_h811.jpg'
                    WHEN 26 THEN 'https://th.bing.com/th/id/OIP.jG0V8qa8H16YWo0zpAbJNgHaE8?w=256&h=180&c=7&r=0&o=7&pid=1.7&rm=3'
                    WHEN 27 THEN 'https://th.bing.com/th/id/OIP.Sixjygb0p5kTGgPPZsr5UgHaEH?w=175&h=104&c=7&bgcl=003559&r=0&o=6&pid=13.1'
                    WHEN 28 THEN 'https://th.bing.com/th/id/R.c5782c09aad9e11283d4db50f8900909?rik=qeEuTsdTTlhlWw&riu=http%3a%2f%2fresource.zhoudaosh.com%2fhomepage%2fwximages%2f20190306%2f2019030609092866335487203.jpg!shoudaosh_img&ehk=XdJVpspK7V9UeeIiBa6Yw7b%2bfnLYcJB4zmi2EbW821k%3d&risl=&pid=ImgRaw&r=0'
                    WHEN 29 THEN 'https://images.chinatimes.com/newsphoto/2022-06-07/656/20220607001468.jpg'
    END
WHERE recommendation_id IN (18,19,20,21,26,27,28,29);