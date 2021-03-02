Feature: Dummy test to check if we do see UI with required components

  Scenario: See Empty Editor
    Given I am on "DocumentPlan" view
    When I click on "NewDocumentPlan"
    Then "NewDocumentPlan" dialog appears