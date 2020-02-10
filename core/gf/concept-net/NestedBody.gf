incomplete concrete NestedBody of Nested = open NestedLex, SyntaxEng, ParadigmsEng, AtLocationEng, CapableOfEng, HasAEng, HasPropertyEng, IsAEng, LocatedNearEng, MadeOfEng, HasAEng in {
  lincat
    DocumentPlan01, Segment02, Amr03 = {s: Str} ;
    -- Turim kitą linketą 
    Amr04 = NP;
  lin
    Function01 Segment02 = {s = Segment02.s} ;
    Function02 Amr03 = {s = Amr03.s} ;
    -- Amr04 be .s
    Function03 Amr04 = {s = (capableOf Amr04 DictionaryItem07 DictionaryItem08).s} ;
    -- _NP funkcijos kvietimas
    Function04 = hasA_NP Data05 Data06 ;
}
