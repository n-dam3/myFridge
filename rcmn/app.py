import pandas as pd
from flask import Flask, request
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

app = Flask(__name__)

column_names = ['레시피번호', '음식이름', '방법별', '상황별', '종류별', '식재료명']
recipe_db = pd.read_csv("recipes.csv", encoding='cp949', names=column_names)

# 중복된 음식이름을 제거
recipe_db = recipe_db.drop_duplicates(subset=['음식이름'])

# 레시피번호를 인덱스로 설정
recipe_db.set_index('레시피번호', inplace=True)

# tfidf_df 데이터 프레임에 저장
tfidf_df = recipe_db[['음식이름', '방법별', '상황별', '종류별', '식재료명']]

# tfidf_df 데이터프레임의 data 컬럼의 단어들의 Tfidf를 계산할 객체 vectorizer 생성
# `min_df`는 단어가 포함된 문서의 최소 개수를 설정
# norm 매개 변수를 사용하여 벡터의 정규화 방법을 지정, "l2"는 유클리드 거리 정규화
vectorizer = TfidfVectorizer(min_df=1, norm="l2")


# 유통기한 임박한 식재료 추천
@app.route('/recommendMenu', methods=['POST'])
def survey():
    categories = request.form.get('categories')
    category_nm = ['밑반찬', '메인반찬', '김치/젓갈/장류', '국/탕', '찌개', '면/만두', '밥/죽/떡', '퓨전', '양식', '샐러드', '양념/소스/잼', '스프', '빵',
                   '디저트', '과자', '차/음료/술']
    category_lst = []

    ingrds = request.form.get('ingrds')
    ingrdList = []
    ingrdList.append(ingrds.replace(',', ' '))

    for category in categories.split(',') :
        category_lst.append(category_nm[int(category)])

    category1 = category_lst[0]
    category2 = category_lst[1]
    category3 = category_lst[2]

    # 전역변수 불러옴
    global tfidf_df

    # 카테고리 설정
    tfidf_df = tfidf_df[
        (tfidf_df['종류별'] == category1) | (tfidf_df['종류별'] == category2)
        | (tfidf_df['종류별'] == category3)
    ]

    tfidf_df['식재료명'] = tfidf_df['식재료명'].str.replace("|", " ")

    tfidf_df['data'] = tfidf_df['방법별'] + " " \
                       + tfidf_df['상황별'] + " " \
                       + tfidf_df['종류별'] + " " \
                       + tfidf_df['식재료명'] + " " \
                       + tfidf_df['음식이름']

    # tfidf_df['data'] 의 Tfidf 를 계산하기 위해서 데이터의 어휘집 구축
    vectorizer.fit(tfidf_df['data'])

    # tfidf_df['data'] 의 Tfidf 를 계산해서 recipe_vector에 저장
    recipe_vector = vectorizer.transform(tfidf_df['data'])
    recipe_vector_df = pd.DataFrame(recipe_vector.toarray(), columns=vectorizer.get_feature_names_out())

    # 사용자 입력을 벡터로 변환
    ingrds_vectorizer = vectorizer.transform(ingrdList)

    # ingrds_vectorizer를 DataFrame으로 변환
    ingrds_vectorizer_df = pd.DataFrame(ingrds_vectorizer.toarray(), columns=vectorizer.get_feature_names_out())

    # receipe_vector_df(전체 레시피)와 ingrds_vectorizer_df(입력한 레시피 식재료)의 유사도를 계산
    cosine_sim = cosine_similarity(recipe_vector_df.values, ingrds_vectorizer_df.values)

    # 유사도를 result_df에 저장
    result_df = pd.DataFrame(cosine_sim.flatten(), columns=['유사도'])

    # result_df에 레시피 번호와 음식이름 추가
    result_df['레시피번호'] = tfidf_df.index
    result_df['음식이름'] = recipe_db['음식이름']
    result_df = result_df.set_index('레시피번호')
    result_df['레시피명'] = recipe_db.loc[result_df.index, '음식이름'].values

    # 유사도가 0.5이상인 값들을 가장 높은 순으로 정렬후 df 생성
    top_df = result_df[result_df['유사도'] >= 0.5].sort_values(by='유사도', ascending=False)

    top_5 = []

    for i in range(len(top_df)):
        # 앞 리스트 값이 뒤 리스트 안에 하나라도 있으면 true
        if any(item in ingrds.split(',') for item in recipe_db.loc[top_df.index[i], '식재료명'].split('|')):
            if len(top_5) == 4 :
                top_5.append(str(top_df.index[i]))
                break
            else :
                top_5.append(str(top_df.index[i]))

    if len(top_5) < 5 :
        for ingrdId, ingrdList in recipe_db['식재료명'].items() :
            if any(item in ingrds.split(',') for item in ingrdList.split('|')) :
                if ingrdList.index not in top_5 :
                    if len(top_5) == 4 :
                        top_5.append(str(ingrdId))
                        break
                    else :
                        top_5.append(str(ingrdId))

    return ' '.join(top_5)

if __name__ == '__main__':
    app.run()