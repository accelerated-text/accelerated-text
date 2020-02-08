resource MadeOfGer = open ParadigmsEng, ConstructorsEng, SyntaxEng in {

  oper -- Kuhlshrank is gefertigted aus Stahl

    madeOf : N -> N -> S =
      \subject,object ->
      (mkS
         (mkCl
            (mkNP subject)
            (mkVP
               (passiveVP (mkV2 (mkV "gefertigt")))
               (ConstructorsEng.mkAdv (mkPrep "aus") (mkNP object))))) ;

}
