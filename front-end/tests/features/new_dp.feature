Feature: Create new DocumentPlan in DocumentPlan View

  Scenario: See Empty Editor
    Given I am on "DocumentPlan" view
    Given Workspace is empty
    When I click on "NewDocumentPlan"
    Then "Document plan" appears in workspace