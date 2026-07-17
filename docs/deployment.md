# Bibliotēkas laidiena sagatavošana

Paredzēts reizi ceturksnī pēc tezaurs.lv ceturkšņa laidiena datu iesaldēšanas sapakot arī Maven Central (https://central.sonatype.com/artifact/lv.ailab.morphology/morphology ) šīs pakas atjauninātu versiju


## Nepieciešamie datu avoti:

Jaunākie dati no tēzaura (ir pateikts, ka darba versijas dati ir gatavi, un ir piekļuve tai datu bāzei)

Jaunākie Treebank / anotēto korpusu dati (https://github.com/LUMII-AILab/Treebank) 


## Vārdnīcas datu atjaunināšana

Vajag rīku https://github.com/LUMII-AILab/TezaursMorphoDump.git , kuram db_config.py jānorāda strādājoša piekļuve tēzaura darba versijas Postgresql datubāzei.

Rīks sagatavos `tezaurs_lexemes.json` (vai arī `tezaurs_latgalian.json` ja skriptu laidīs ar parametru latgalian), kas jāieliek morfoloģijas pakā zem `src/main/resources/`

Vajadzētu arī atjaunināt `Statistics.xml` failu. To no vārdnīcas un korpusa datiem (kuru atjaunināšana ir zemāk) uzģenerē skripts `CorpusProcessing.java`, tas to uzģenerēs zem projekta pamatmapes, lai to atjauninātu, tas arī jāieliek zem `src/main/resources/`


## Korpusa datu atjaunināšana

Vajag skriptus no https://github.com/LUMII-AILab/TreebankTools un strādājošu Perl vidi.

Uz Linux:
	```
		sudo apt-get install libxml-libxslt-perl
	```
Uz OS X:
	```
		curl -L https://install.perlbrew.pl | bash   
		source ~/perl5/perlbrew/etc/bashrc
		perlbrew install perl-5.16.0   
		perlbrew switch perl-5.16.0
		sudo cpan -i XML::LibXSLT
	```
Citiem _TreebankTools_ skriptiem var vajadzēt arī `sudo cpan -i XML::Simple`, bet failu savienotājs, ko lieto `preparePOSTagData.sh`, tos šobrīd nesauc.

Palaižam `TreebankTools/Scripts/preparePOSTagData.sh`, kam vajadzētu sakopēt jaunākos datus zem `../morphology/src/main/resources/`


## Versijas atjaunināšana

Izlaiž testus un paskatās vai `MorphoEvaluate` rezultātos nav būtisku procentu kritumu un rupju kļūdu:
- TagSetTest
- MorphologyTest
- LatgalianTest
- MorphoEvaluate

`pom.xml` jāatjaunina laidiena versija, un ar `mvn clean deploy` tas varētu nonākt maven central, ja ir šādi priekšnosacījumi.
- Pareizi piekļuves parametri `~/.m2/settings.xml`:
	- https://central.sonatype.com/ profila sadaļā "View user tokens" poga "Generate Token" iedos XMLu,
	- XMLā `${server}` aizstāj ar `central`
	- attiecīgo XML ieliek `settings.xml` tā, lai sanāk
	 `<settings><servers><server><id>central</id><username>???</username><password>???</password></server></servers></settings>`
- Nopublicēta `gpg` atslēga pakas parakstīšanai:
	- ar komandu `gpg --list-keys` var dabūt visu jau saģenerēto `gpg` atslēgu sarakstu
	- ar komandu `gpg --keyserver keys.openpgp.org --send-keys XXX` nopublisko atslēgu XXX lietošanai pakas parakstīšanai
	- vairāk info https://central.sonatype.org/publish/requirements/gpg/


## Nākamie soļi pēc morfoloģijas versijas atjaunināšanas

LVTager blokā (https://github.com/LUMII-AILab/LVTagger/)
- Jāatjaunina `pom.xml` atkarības
- `mvn clean install`vai `mvn clean install -P with-dep`, lai atsaucas uz svaigākajām atkarībām
- Pozitīvi var būt arī apmācīt jaunu produkcijas modeli tagerim `./morpho_train.sh -production` kas uz 2025. MacbookPro iet 45min
- Rezultātus pieraksta `MorphoCRF/morfoCRFeksperimenti.txt`
- Produkcijas modeļus pako atsevišķā maven pakā, kas ir mapītē `morphomodel`
- Uz Maven Central bez atkarībām publicēšana ir `mvn clean deploy`


Webservisu blokā (https://github.com/LUMII-AILab/Webservices)
- Jāatjaunina `pom.xml` atkarības
- Uz Maven Central bez atkarībām publicēšana ir `mvn clean deploy`
- Uz `api.tezaurs.lv` kopē `mvn clean install -P with-dep` rezultātu
- `sudo service tezaurs-api restart`
