package io.virtue.core.manager;

import io.virtue.core.*;
import org.intellij.lang.annotations.Language;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Abstract rule manager.
 *
 * @param <T>
 */
public abstract class AbstractRuleManager<T> extends AbstractManager<T> {

    protected final Map<T, RuleWrapper<T>> ruleMap = new HashMap<>();

    protected AbstractRuleManager(Virtue virtue) {
        super(virtue);
    }

    @Override
    public void clear() {
        super.clear();
        ruleMap.clear();
    }

    /**
     * Add protocol rule.
     *
     * @param config
     * @param scope
     * @param rules
     */
    public void addProtocolRule(T config, MatchScope scope, @Language("RegExp") String... rules) {
        RuleWrapper<T> wrapper = ruleMap.computeIfAbsent(config, RuleWrapper::new);
        if (scope == MatchScope.INVOKER) {
            wrapper.addProtocolRulesForCaller(rules);
            wrapper.addProtocolRulesForCallee(rules);
        } else if (scope == MatchScope.CALLER) {
            wrapper.addProtocolRulesForCaller(rules);
        } else if (scope == MatchScope.CALLEE) {
            wrapper.addProtocolRulesForCallee(rules);
        }
    }

    /**
     * Add path rule.
     *
     * @param config
     * @param scope
     * @param rules
     */
    public void addPathRule(T config, MatchScope scope, @Language("RegExp") String... rules) {
        RuleWrapper<T> ruleWrapper = ruleMap.computeIfAbsent(config, RuleWrapper::new);
        if (scope == MatchScope.INVOKER) {
            ruleWrapper.addPathRulesForCaller(rules);
            ruleWrapper.addPathRulesForCallee(rules);
        } else if (scope == MatchScope.CALLER) {
            ruleWrapper.addPathRulesForCaller(rules);
        } else if (scope == MatchScope.CALLEE) {
            ruleWrapper.addPathRulesForCallee(rules);
        }
    }

    /**
     * Execute rules.
     */
    public void executeRules() {
        List<Callee<?>> allCallee = virtue.configManager().remoteServiceManager().allCallee();
        List<Caller<?>> allCaller = virtue.configManager().remoteCallerManager().allCaller();
        for (RuleWrapper<T> ruleWrapper : ruleMap.values()) {
            List<Callee<?>> matchedCallee = matchInvokers(ruleWrapper.calleeRegexWrapper(), allCallee);
            List<Caller<?>> matchedCaller = matchInvokers(ruleWrapper.callerRegexWrapper(), allCaller);
            doExecuteRules(ruleWrapper.config, matchedCallee, matchedCaller);
        }
    }

    protected abstract void doExecuteRules(T config, List<Callee<?>> matchedCallee, List<Caller<?>> matchedCaller);

    protected <I extends Invoker<?>> List<I> matchInvokers(RuleWrapper.RegexWrapper wrapper, List<I> invokers) {
        List<String> protocolRules = wrapper.protocolRules;
        List<String> pathRules = wrapper.pathRules;
        return invokers.stream().filter(invoker -> {
            boolean protocolResult = protocolRules.isEmpty();
            boolean pathResult = pathRules.isEmpty();
            for (String protocolRule : protocolRules) {
                if (isMatch(protocolRule, invoker.protocol())) {
                    protocolResult = true;
                }
            }
            for (String pathRule : pathRules) {
                if (isMatch(pathRule, invoker.path())) {
                    pathResult = true;
                }
            }
            return protocolResult && pathResult;
        }).collect(Collectors.toList());
    }

    private boolean isMatch(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content).find();
    }

    protected static class RuleWrapper<T> {

        private final T config;

        private volatile RegexWrapper callerRegexWrapper;

        private volatile RegexWrapper calleeRegexWrapper;

        public RuleWrapper(T config) {
            this.config = config;
        }

        public void addProtocolRulesForCaller(String... rules) {
            RegexWrapper regexWrapper = callerRegexWrapper();
            regexWrapper.addProtocolRules(rules);
        }

        public void addPathRulesForCaller(String... rules) {
            RegexWrapper regexWrapper = callerRegexWrapper();
            regexWrapper.addPathRules(rules);
        }

        public void addProtocolRulesForCallee(String... rules) {
            RegexWrapper regexWrapper = calleeRegexWrapper();
            regexWrapper.addProtocolRules(rules);
        }

        public void addPathRulesForCallee(String... rules) {
            RegexWrapper regexWrapper = calleeRegexWrapper();
            regexWrapper.addPathRules(rules);
        }

        public T config() {
            return config;
        }

        public RegexWrapper callerRegexWrapper() {
            if (callerRegexWrapper == null) {
                synchronized (this) {
                    if (callerRegexWrapper == null) {
                        callerRegexWrapper = new RegexWrapper();
                    }
                }
            }
            return callerRegexWrapper;
        }

        public RegexWrapper calleeRegexWrapper() {
            if (calleeRegexWrapper == null) {
                synchronized (this) {
                    if (calleeRegexWrapper == null) {
                        calleeRegexWrapper = new RegexWrapper();
                    }
                }
            }
            return calleeRegexWrapper;
        }

        protected static class RegexWrapper {

            final List<String> protocolRules = new LinkedList<>();

            final List<String> pathRules = new LinkedList<>();

            public void addProtocolRules(String... rules) {
                if (rules != null) {
                    Arrays.stream(rules).filter(Objects::nonNull).forEach(protocolRules::add);
                }
            }

            public void addPathRules(String... rules) {
                if (rules != null) {
                    Arrays.stream(rules).filter(Objects::nonNull).forEach(pathRules::add);
                }
            }

        }
    }
}
