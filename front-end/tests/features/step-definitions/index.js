import { After, Before, Given } from '@cucumber/cucumber';

Before(async function(testCase) {
    return await this.openAcceleratedText();
});

After(async function() {
    return await this.closeAcceleratedText();
});

import * as g from './given';
import * as t from './then';
import * as w from './when';