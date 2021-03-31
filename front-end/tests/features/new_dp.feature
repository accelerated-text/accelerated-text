Feature: Create new DocumentPlan in DocumentPlan View

  Scenario: See Empty Editor
    Given I am on "DocumentPlan" view
    Given Workspace is without errors
    When I click on "NewDocumentPlan"
    Then "Document plan" appears in workspace