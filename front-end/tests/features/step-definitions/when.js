import { When } from '@cucumber/cucumber';

When("I click on {string}", async function(b) {
    var selector;
    if(b == "NewDocumentPlan"){
        selector = ".qa-tests-add-example-segment";
    }
    await this.page.click(selector);
    await this.page.keyboard.press("Enter");
})