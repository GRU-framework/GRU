package org.gruth.script

import org.gruth.reports.TztReport
import org.gruth.utils.TaggedObject
import org.gruth.utils.TaggedsProvider

/**
 *
 * @author bamade
 */
// Date: 02/04/15

class MultipleInstanceContext implements ExecutionContext{
    TaggedsProvider provider ;
    Closure code ;
    Production production

    MultipleInstanceContext(TaggedsProvider provider, Closure code) {
        this.provider = provider
        this.code = code
        production = new Production(this)
        production._withEach (code)
    }

    @Override
    Production xx_xcuteTests(Production production) {
        for(TaggedObject tagged : provider) {
           production.accumulate(tagged)
        }
        return production
    }

    @Override
    TztReport get_report() {
        return null
    }
}
