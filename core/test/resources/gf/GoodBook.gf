abstract GoodBook = {
  flags
    startcat = Sentence;
  cat
    Sentence; Data; Modifier;
  fun
    GoodTitle : Modifier -> Data -> Sentence;
    DataTitle : Data;
    GoodModifier : Modifier;
}