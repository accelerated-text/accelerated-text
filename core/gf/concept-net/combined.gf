resource combined = open CapableOfEng, HasAEng, ParadigmsEng, SyntaxEng in {

  oper allInOne : CN -> CN -> V2 -> CN -> Text =
         \subject, objectAttr, verb, objectAction ->

         (capableOf
            (hasA_NP subject objectAttr)
            verb
            objectAction);

}
