resource UtilsEst = open SyntaxEst, ParadigmsEst, (R=ResEst) in {

  -- Combine modifiers
  oper combineMods = overload {
         combineMods : AP -> AP -> AP = \x, y -> mkAP and_Conj (mkListAP x y);
         combineMods : AP -> ListAP -> AP = \x, xs -> mkAP and_Conj (mkListAP x xs);
         };
}
