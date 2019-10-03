<?xml version="1.0"?>
<!-- 
Copyright (C) 2003-4 University of Edinburgh (Michael White) 
$Revision: 1.40 $, $Date: 2005/11/16 18:14:50 $ 

NB: These namespace declarations seem to work with the version of Xalan 
    that comes with JDK 1.4.  With newer versions of Xalan, 
    different namespace declarations may be required. 
-->
<xsl:transform 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
  version="1.0"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xalan2="http://xml.apache.org/xslt"
  exclude-result-prefixes="xalan xalan2">
  

  <!-- ***** Import Core Dictionary Definitions ***** -->
  <xsl:import href="../core-en/dict.xsl"/>
  
  <xsl:output indent="yes" xalan2:indent-amount="2"/> 
  <xsl:strip-space elements="*"/>

  
  <!-- ***** Start Output Here ***** -->
  <xsl:template match="/">
  <dictionary name="comic"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:noNamespaceSchemaLocation="../dict.xsd"
  >
  
  <!-- Add core entries -->
  <xsl:call-template name="add-entries"/>

  
  <!-- Prepositions -->
  <entry stem="at" pos="Prep">
    <member-of family="Prep-Nom"/>
  </entry>
  <entry stem="by" pos="Prep">
    <member-of family="By-Creator"/>
  </entry>
  <entry stem="from" pos="Prep">
    <member-of family="Prep-Nom"/>
    <member-of family="Prep-Source"/>
  </entry>
  <entry stem="in" pos="Adj">
    <member-of family="Prep-Loc"/>
  </entry>
  <entry stem="in_addition_to" pos="Adv">
    <member-of family="Prep-Transitional" pred="in-addition-to"/>
  </entry>
  <entry stem="of" pos="Prep">
    <member-of family="Prep-Nom"/>
  </entry>
  <entry stem="on" pos="Adj">
    <member-of family="Prep-Loc"/>
  </entry>
  <entry stem="to" pos="Prep">
    <member-of family="To-Infinitive"/>
  </entry>
  

  <!-- Adverbs -->
  <entry stem="again" pos="Adv">
    <member-of family="Adverb"/>
  </entry>
  <entry stem="before" pos="Adv">
    <member-of family="Adverb"/>
  </entry>
  <entry stem="earlier" pos="Adv">
    <member-of family="Adverb"/>
  </entry>
  <entry stem="down_here" pos="Adv">
    <member-of family="Loc-Adverb"/>
  </entry>
  <entry stem="here" pos="Adv">
    <member-of family="Loc-Adverb"/>
  </entry>
  <entry stem="now" pos="Adv">
    <member-of family="Transitional-Adverb"/>
  </entry>
  <entry stem="once_again" pos="Adv">
    <member-of family="Adverb" pred="once-again"/>
  </entry>
  
  
  <!-- Conjunctions -->
  <entry stem="as" pos="Conj">
    <member-of family="Subconj-Transitional"/>
  </entry>
  
  
  <!-- Verbs -->
  <entry stem="draw" pos="V">
    <member-of family="Drawing-From" pred="draw-from"/>
    <word form="draw" macros="@base"/>
    <word form="drawing" macros="@ng"/>
    <word form="draw" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="draws" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="draw" macros="@pres @pl-agr"/>
    <word form="drew" macros="@past"/>
  </entry>

  <entry stem="feature" pos="V">
    <member-of family="Featuring"/>
    <word form="feature" macros="@base"/>
    <word form="featuring" macros="@ng"/>
    <word form="feature" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="features" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="feature" macros="@pres @pl-agr"/>
    <word form="featured" macros="@past"/>
  </entry>

  <entry stem="go" pos="V">
    <member-of family="Going-With" pred="go-with"/>
    <word form="go" macros="@base"/>
    <word form="going" macros="@ng"/>
    <word form="go" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="goes" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="go" macros="@pres @pl-agr"/>
    <word form="went" macros="@past"/>
  </entry>

  <entry stem="look-at" pos="V">
    <member-of family="Looking-At"/>
    <word form="look" macros="@base"/>
    <word form="looking" macros="@ng"/>
    <word form="look" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="looks" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="look" macros="@pres @pl-agr"/>
    <word form="looked" macros="@past"/>
  </entry>

  <entry stem="mean" pos="V">
    <member-of family="Meaning"/>
    <word form="mean" macros="@base"/>
    <word form="meaning" macros="@ng"/>
    <word form="mean" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="means" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="mean" macros="@pres @pl-agr"/>
    <word form="meant" macros="@past"/>
  </entry>

  <entry stem="see" pos="V">
    <member-of family="Seeing"/>
    <word form="see" macros="@base"/>
    <word form="seeing" macros="@ng"/>
    <word form="see" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="sees" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="see" macros="@pres @pl-agr"/>
    <word form="saw" macros="@past"/>
  </entry>

  <entry stem="use" pos="V">
    <member-of family="Using"/>
    <word form="use" macros="@base"/>
    <word form="using" macros="@ng"/>
    <word form="use" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="uses" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="use" macros="@pres @pl-agr"/>
    <word form="used" macros="@past"/>
  </entry>

  <entry stem="want" pos="V">
    <member-of family="Experiencer-Subj"/>
    <word form="want" macros="@base"/>
    <word form="wanting" macros="@ng"/>
    <word form="want" macros="@pres @sg-agr @non-3rd-agr"/>
    <word form="wants" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="want" macros="@pres @pl-agr"/>
    <word form="wanted" macros="@past"/>
  </entry>


  <!-- Canned VPs -->
  <entry stem="help-set-mood" pos="V" class="colour_prop">
    <member-of family="Canned-VP"/>
    <word form="helping_to_set_the_mood" macros="@ng"/>
    <word form="helps_to_set_the_mood" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="help_to_set_the_mood" macros="@pres @pl-agr"/>
  </entry>

  <entry stem="emphasise-straight-lined-character" pos="V" class="colour_prop">
    <member-of family="Canned-VP"/>
    <word form="emphasising_the_clear,_straight_lined_character_of_your_bathroom_in_a_stylish_way" macros="@ng"/>
    <word form="emphasises_the_clear,_straight_lined_character_of_your_bathroom_in_a_stylish_way" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="emphasise_the_clear,_straight_lined_character_of_your_bathroom_in_a_stylish_way" macros="@pres @pl-agr"/>
  </entry>

  <entry stem="give-tuscan-feeling" pos="V" class="colour_prop">
    <member-of family="Canned-VP"/>
    <word form="giving_the_room_the_feeling_of_a_Tuscan_country_home" macros="@ng"/>
    <word form="gives_the_room_the_feeling_of_a_Tuscan_country_home" macros="@pres @sg-or-mass-agr @3rd-agr"/>
    <word form="give_the_room_the_feeling_of_a_Tuscan_country_home" macros="@pres @pl-agr"/>
  </entry>

  
  <!-- Adjectives -->
  <entry stem="based_on" pos="Adj">
    <member-of family="Based-On" pred="based-on"/>
  </entry>

  <entry stem="more" pos="Adj">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="other" pos="Adj">
    <member-of family="Adjective"/>
  </entry>
  
  <entry stem="abstract" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="animal" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="belt_buckle" pos="Adj" class="quality">
    <member-of family="Adjective" pred="belt-buckle"/>
  </entry>
  <entry stem="comic" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="cross" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="diamond" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="decorative" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="face" pos="Adj" class="quality">  <!-- nb: nouns like 'face' for 'face-motifs' -->
    <member-of family="Adjective"/>
  </entry>
  <entry stem="female" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="floral" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="geometric" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="jeans" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="male" pos="Adj" class="quality">
    <member-of family="Adjective"/>
  </entry>
  
  <entry stem="classic" pos="Adj" class="style">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="country" pos="Adj" class="style">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="family" pos="Adj" class="style">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="modern" pos="Adj" class="style">
    <member-of family="Adjective"/>
  </entry>
  
  <entry stem="larger" pos="Adj" class="size">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="smaller" pos="Adj" class="size">
    <member-of family="Adjective"/>
  </entry>
  
  <!-- Colours (adj and noun) -->
  <entry stem="beige" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="beige" pos="N" macros="@mass" class="colour"/>
  <entry stem="black" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="black" pos="N" macros="@mass" class="colour"/>
  <entry stem="blue" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="blue" pos="N" macros="@mass" class="colour"/>
  <entry stem="brown" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="brown" pos="N" macros="@mass" class="colour"/>
  <entry stem="cream" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="cream" pos="N" macros="@mass" class="colour"/>
  <entry stem="dark_green" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="dark_green" pos="N" macros="@mass" class="colour"/>
  <entry stem="dark_red" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="dark_red" pos="N" macros="@mass" class="colour"/>
  <entry stem="gray" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="gray" pos="N" macros="@mass" class="colour"/>
  <entry stem="grey" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="grey" pos="N" macros="@mass" class="colour"/>
  <entry stem="green" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="green" pos="N" macros="@mass" class="colour"/>
  <entry stem="indigo" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="indigo" pos="N" macros="@mass" class="colour"/>
  <entry stem="ochre" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="ochre" pos="N" macros="@mass" class="colour"/>
  <entry stem="off_white" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="off_white" pos="N" macros="@mass" class="colour"/>
  <entry stem="orange" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="orange" pos="N" macros="@mass" class="colour"/>
  <entry stem="pink" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="pink" pos="N" macros="@mass" class="colour"/>
  <entry stem="purple" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="purple" pos="N" macros="@mass" class="colour"/>
  <entry stem="red" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="red" pos="N" macros="@mass" class="colour"/>
  <entry stem="rose" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="rose" pos="N" macros="@mass" class="colour"/>
  <entry stem="royal_blue" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="royal_blue" pos="N" macros="@mass" class="colour"/>
  <entry stem="salmon" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="salmon" pos="N" macros="@mass" class="colour"/>
  <entry stem="sandstone" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="sandstone" pos="N" macros="@mass" class="colour"/>
  <entry stem="silver" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="silver" pos="N" macros="@mass" class="colour"/>
  <entry stem="terracotta" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="terracotta" pos="N" macros="@mass" class="colour"/>
  <entry stem="violet" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="violet" pos="N" macros="@mass" class="colour"/>
  <entry stem="white" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="white" pos="N" macros="@mass" class="colour"/>
  <entry stem="yellow" pos="Adj" class="colour">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="yellow" pos="N" macros="@mass" class="colour"/>

  
  <!-- Nouns -->
  <entry stem="art" pos="N" macros="@mass" class="decoration"/>
  <entry stem="artwork" pos="N" macros="@mass" class="decoration"/>
  <entry stem="collection" pos="N" class="abstraction">
    <word form="collection" macros="@sg"/>
    <word form="collections" macros="@pl"/>
  </entry>
  <entry stem="colour" pos="N" class="abstraction">
    <word form="colour" macros="@sg"/>
    <word form="colours" macros="@pl"/>
  </entry>
  <entry stem="colour_scheme" pos="N" class="mental-obj">
    <word form="colour_scheme" macros="@sg"/>
    <word form="colour_schemes" macros="@pl"/>
  </entry>
  <entry stem="design" pos="N" class="mental-obj">
    <word form="design" macros="@sg"/>
    <word form="designs" macros="@pl"/>
  </entry>
  <entry stem="fruit" pos="N" macros="@mass" class="decoration"/>
  <entry stem="mosaics" pos="N" macros="@pl" class="decoration"/>
  <entry stem="motif" pos="N" class="decoration">
    <word form="motif" macros="@sg"/>
    <word form="motifs" macros="@pl"/>
  </entry>
  <entry stem="option" pos="N" class="mental-obj">
    <word form="option" macros="@sg"/>
    <word form="options" macros="@pl"/>
  </entry>
  <entry stem="series" pos="N" class="abstraction">
    <word form="series" macros="@sg"/>
    <word form="series" macros="@pl"/>
  </entry>
  <entry stem="shape" pos="N" class="decoration">
    <word form="shape" macros="@sg"/>
    <word form="shapes" macros="@pl"/>
  </entry>
  <entry stem="style" pos="N" class="abstraction">
    <word form="style" macros="@sg"/>
    <word form="styles" macros="@pl"/>
  </entry>
  <entry stem="tile" pos="N" class="phys-obj">
    <word form="tile" macros="@sg"/>
    <word form="tiles" macros="@pl"/>
  </entry>
  
  
  <!-- Proper Names (with some also as adjectives, for now) -->
  <entry stem="Agrob_Buchtal" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Alessi_Tiles" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Aparici" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Apavisa" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Bisazza" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Cerim_Ceramiche" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Coem" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Engers_Eurodesign" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Gardenia_Orchidea" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Imola_Ceramica" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Iris_Ceramica" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="NovaBell" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Porcelaingres" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Porcelanosa" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Sphinx_Tiles" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Steuler" pos="NNP" class="manufacturer" macros="@sg-2"/>
  <entry stem="Villeroy_and_Boch" pos="NNP" class="manufacturer" macros="@sg-2"/>

  <entry stem="Abbazie" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Abbazie" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Aguamarina" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Aguamarina" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Alt_Mettlach" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Alt_Mettlach" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Altamira" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Altamira" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Amazonita" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Amazonita" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Anasol" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Anasol" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Aramis" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Aramis" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Asterix" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Asterix" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Armonie" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Armonie" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Ateliers" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Ateliers" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Blue_Jeans" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Blue_Jeans" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Carioca" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Carioca" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Cardiff" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Cardiff" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Century" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Century" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Century_Esprit" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Century_Esprit" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Colorado" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Colorado" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Creative_System_Amazonas" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Creative_System_Amazonas" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Creative_System_Safari" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Creative_System_Safari" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Darlington" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Darlington" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Funny_Day" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Funny_Day" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Girotondo_Tile" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Girotondo_Tile" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Helenus" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Helenus" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Hippo" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Hippo" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="HundertWasser" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="HundertWasser" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="I_marmi" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="I_marmi" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="I_marmi_Art" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="I_marmi_Art" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Jazz" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Jazz" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Kerarock" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Kerarock" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Le_Pietre" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Le_Pietre" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Levante" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Levante" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Lollipop" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Lollipop" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Opus_Romano" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Opus_Romano" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Palace" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Palace" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Sandstein" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Sandstein" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Smart" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Smart" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Tirrenia" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Tirrenia" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>
  <entry stem="Viso_Tile" pos="NNP" class="series" macros="@sg-2"/>
  <entry stem="Viso_Tile" pos="Adj" class="series">
    <member-of family="Adjective"/>
  </entry>

  
  <!-- Gesture Instances -->
  <entry word="GI-00:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-01:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-03:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-25:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-28:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-29:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-30:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-31:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-32:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-33:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-34:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-35:GC-e" stem="*circ*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-36:GC-w" stem="*wave*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-37:GC-w" stem="*wave*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-40:GC-w" stem="*wave*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-41:GC-w" stem="*wave*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>
  <entry word="GI-44:GC-w" stem="*wave*" pos="Gest" coart="true">
    <member-of family="Gesture"/>
  </entry>

  <!-- Nods (for example) -->
  <entry word="NI-00" stem="*nod*" pos="Nod" coart="true">
    <member-of family="Nod"/>
  </entry>

  
  <!-- Add core macros -->
  <xsl:call-template name="add-macros"/>
  
  </dictionary>
  </xsl:template>
</xsl:transform>

