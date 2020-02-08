abstract Kettle = {
  cat Description;
      IsA; MadeOf;
      IsA_Sbj; IsA_Attr;
      MadeOf_Sbj; MadeOf_Obj;

  fun kettle : IsA -> MadeOf -> Description;

      isa : IsA_Sbj -> IsA_Attr -> IsA;
      madeof : MadeOf_Sbj -> MadeOf_Obj -> MadeOf;

      isa_sbj : IsA_Sbj;
      isa_attr : IsA_Attr;
      modeof_sbj : MadeOf_Sbj;
      madeof_obj : MadeOf_Obj;
}
