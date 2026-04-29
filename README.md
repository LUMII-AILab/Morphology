# Morphological Analyzer for Latvian Language

 A Java library for analyzing morphology and part of speech information for Latvian words.
 Accurate analysis is based on lexeme data periodically updated from Tezaurs.lv database.
 Also includes generation of all inflections of a word, and crude statistical disambiguation for analysis.
 

## BASIC USAGE

```
 Analyzer analyzer = new Analyzer();
 
 // analysis
 Word result = analyzer.analyze("roku");
 for (Wordform wf : result.wordforms) {
	wf.describe();
 }

 // generation of inflections
 List<Wordform> wordforms = analyzer.generateInflections("rakt");
 for (Wordform wf : wordforms) {
	wf.describe();
 }
```

 Review unit tests for more examples.


## INSTALLATION
  
Use maven to build and deploy.
The published releases should be available at Maven Central https://central.sonatype.com/artifact/lv.ailab.morphology/morphology

Packaging instructions at `docs/deployment.md`


## LICENCE

(c) Institute of Mathematics and Computer Science, University of Latvia, 2005-2026

This software is licenced under GNU General Public Licence.
Commercial licencing is available if neccessary, contact us at lauma@ailab.lv.


## REFERENCES
 Current usage is described at http://www.ep.liu.se/ecp_article/index.en.aspx?issue=085;article=024
 The initial core algorithm is published at http://www.semti-kamols.lv/doc_upl/Kamols-Kaunas-paper-3.pdf


## Acknowledgements

Work on morphological toolkit has been carried out through various projects since at least 2005. The author of the core algorithm is Pēteris Paikens. Since 2026-02 development of the library continues here, in this repository.

The work on porting Tezaurs.lv inflectional paradigms to GF and creating a wide-coverage computational GF lexicon for Latvian was funded by the Latvian Council of Science under the grant agreement lzp-2022/1-0443 ([Advancing Latvian Computational Lexical Resources for Natural Language Understanding and Generation](https://wordnet.ailab.lv/project2)).


