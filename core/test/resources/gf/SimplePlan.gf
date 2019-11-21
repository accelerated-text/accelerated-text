abstract SimplePlan = {
 flags
  startcat = DocumentPlan;
 cat
  DocumentPlan; Instance; Segment; Data;
 fun
  DocumentPlan01 : Segment -> DocumentPlan;
  Segment01 : Instance -> Segment;
  Instance01 : Data -> Instance;
  Data01 : Data;
}