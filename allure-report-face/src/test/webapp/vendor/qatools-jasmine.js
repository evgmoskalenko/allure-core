/**
 * jasmine helpers pack
 */
/*global angular, beforeEach, afterEach, jasmine*/
(function(jasmine) {
    'use strict';
    if(!jasmine) {
        return;
    }
    jasmine.qatools = {
        triggerMouseEvent: function(el, type) {
            var ev = document.createEvent("MouseEvent");
            ev.initMouseEvent(
                type,
                true /* bubble */, true /* cancelable */,
                window, null,
                0, 0, 0, 0, /* coordinates */
                false, false, false, false, /* modifier keys */
                0 /*left*/, null
            );
            el.dispatchEvent(ev);
        },
        mockD3Tooltip: function() {
            var tooltipsCount = 0;
            beforeEach(module(function($provide) {
                $provide.value('d3Tooltip', jasmine.createSpy('tooltipConstructor').and.callFake(function() {
                    tooltipsCount++;
                    return angular.extend(jasmine.createSpyObj('tooltip', ['show', 'hide']), {destroy: function() {
                        tooltipsCount--;
                    }});
                }));
            }));
            afterEach(function() {
                expect(tooltipsCount).toBe(0);
            });
        },
        fakePluginApi: function() {
            beforeEach(module(function($provide) {
                $provide.provider('testcase', function() {
                    return jasmine.createSpyObj('testcaseProvider', ['$get', 'attachStates']);
                });
                $provide.provider('allurePlugins', function() {
                    var allureTabsProvider = jasmine.createSpyObj('allurePluginsProvider', ['$get', 'addTranslation', 'addTab', 'addWidget']);
                    allureTabsProvider.tabs = [];
                    allureTabsProvider.$get.and.returnValue(allureTabsProvider.tabs);
                    return allureTabsProvider;
                });
            }));
        }
    };
    beforeEach(function() {
        jasmine.addMatchers({
            toHaveClass: function() {
                return {
                    compare: function(actual, expected) {
                        var pass = actual.hasClass(expected);
                        return {
                            pass: pass,
                            message: "Expected '" + angular.mock.dump(actual) + "'" + (pass ? ' not' : '') + " to have class '" + expected + "'."
                        };
                    }
                };
            },
            toEqualData: function() {
                return {
                    compare: function(actual, expected) {
                        return {
                            pass: angular.equals(actual, expected)
                        };
                    }
                };

            }
        });
    });
})(jasmine);

