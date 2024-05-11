import { getInfo as detail } from "/@/views/rule-engine/helper/api";
import { ref } from "vue";
import { defineStore } from "pinia";
import type { FormModelType } from "/@/views/rule-engine/Scene/typings";
import { cloneDeep, isArray } from "lodash-es";
import { randomString } from "/@/utils/utils";

const assignmentKey = (data: any[]): any[] => {
    const onlyKey = ["when", "then", "terms", "actions"];
    if (!data) return [];

    return data.map((item: any) => {
        if (item) {
            item.key = randomString();
            Object.keys(item).some((key) => {
                if (onlyKey.includes(key) && isArray(item[key])) {
                    item[key] = assignmentKey(item[key]);
                }
            });
        }
        return item;
    });
};

export const defaultBranches = [
    {
        when: [
            {
                terms: [
                    {
                        column: undefined,
                        value: {
                            source: "fixed",
                            value: undefined
                        },
                        termType: undefined,
                        key: "params_1",
                        type: "and"
                    }
                ],
                type: "and",
                key: "terms_1"
            }
        ],
        key: "branches_1",
        shakeLimit: {
            enabled: false,
            time: 1,
            threshold: 1,
            alarmFirst: false
        },
        then: []
    }
];

const defaultOptions = {
    trigger: {},
    when: [
        {
            terms: [
                {
                    terms: [
                        ["", "eq", "", "and"]
                    ]
                }
            ]
        }
    ]
};

export const useSceneStore = defineStore("scene", () => {

    const data = ref<FormModelType>({
        trigger: { type: ''},
        options: defaultOptions,
        branches: defaultBranches,
        description: '',
        name: '',
        id: undefined
    });

    const productCache = {};

    const refresh = () => {
        data.value = {
            trigger: { type: ''},
            options: cloneDeep(defaultOptions),
            branches: cloneDeep(defaultBranches),
            description: '',
            name: '',
            id: undefined
        }
    };
    const getDetail = async (id: string) => {
        refresh();
        const resp = await detail(id);
        console.info("获取场景的详细信息：", resp);
        if (resp.data) {
            const result = resp.data as any;
            let triggerType = result.triggerType;
            let branches: any[] = result.branches;

            if (!branches) {
                branches = cloneDeep(defaultBranches)
                if (triggerType === 'device') {
                    branches.push(null)
                } else {
                    branches[0].when.length = []
                }
            } else {
                const branchesLength = branches.length;
                if (
                    triggerType === 'device' &&
                    ((branchesLength === 1 && branches[0]?.when?.length) || // 有一组数据并且when有值
                        (branchesLength > 1 && branches[branchesLength - 1]?.when?.length)) // 有多组否则数据，并且最后一组when有值
                ) {
                    branches.push(null);
                }
            }
            console.log("branches", branches);
            data.value = {
                ...result,
                trigger: result.trigger || {},
                branches: cloneDeep(assignmentKey(branches)),
                options: result.options ? {...cloneDeep(defaultOptions), ...result.options } : cloneDeep(defaultOptions),
            }
            console.info("获取场景的详细信息2：", data);
        }
    };

    return {
        data,
        productCache,
        getDetail,
        refresh
    };
});
