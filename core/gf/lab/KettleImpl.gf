--# -path=../concept-net
concrete KettleImpl of Kettle =
  open IsAEng, MadeOfEng, ParadigmsEng, SyntaxEng, UtilsEng in  {

  lincat Description = Text;
         IsA, MadeOf = S;
         IsA_Sbj, IsA_Attr = NP;
         MadeOf_Sbj, MadeOf_Obj = N;

  lin kettle isa madeof = mkText (mkText isa) (mkText madeof);

      isa subject attribute = isA_S subject attribute;
      madeof subject object = madeOf subject object;

      isa_sbj = mkAMod
        the_Det
        (combineMods (mkAP (mkA "fearsome")) (mkAP (mkA "powerful")))
        (mkN "T1000");

      isa_attr = mkAMod a_Det (mkAP (mkA "red")) (mkN "kettle");
      modeof_sbj = mkN "kettle";
      madeof_obj = mkN "glass";
}
